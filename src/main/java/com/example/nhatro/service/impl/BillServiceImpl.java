package com.example.nhatro.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nhatro.dto.request.BillRequestDTO.ConfirmPaymentRequestDTO;
import com.example.nhatro.dto.request.BillRequestDTO.CreateBillRequestDTO;
import com.example.nhatro.dto.request.BillRequestDTO.UpdateBillRequestDTO;
import com.example.nhatro.dto.response.BillResponseDTO;
import com.example.nhatro.entity.Bill;
import com.example.nhatro.entity.Contract;
import com.example.nhatro.entity.Owner;
import com.example.nhatro.entity.PaymentHistory;
import com.example.nhatro.entity.Tenant;
import com.example.nhatro.entity.User;
import com.example.nhatro.enums.BillStatus;
import com.example.nhatro.enums.ContractStatus;
import com.example.nhatro.exception.ResourceNotFoundException;
import com.example.nhatro.dto.response.PaymentHistoryResponseDTO;
import com.example.nhatro.repository.BillRepository;
import com.example.nhatro.repository.ContractRepository;
import com.example.nhatro.repository.OwnerRepository;
import com.example.nhatro.repository.PaymentHistoryRepository;
import com.example.nhatro.repository.TenantRepository;
import com.example.nhatro.repository.UserRepository;
import com.example.nhatro.service.BillService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    private final TenantRepository tenantRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Override
    @Transactional
    public BillResponseDTO createBill(CreateBillRequestDTO request) {
        Long ownerId = getCurrentOwnerId();
        
        Contract contract = null;
        if (request.getContractId() != null) {
            contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contract not found with ID: " + request.getContractId()));
            // Verify contract belongs to current owner
            if (!contract.getOwner().getOwnerId().equals(ownerId)) {
                throw new ResourceNotFoundException("Contract not found with ID: " + request.getContractId());
            }
        } else if (request.getRoomCode() != null) {
            contract = contractRepository.findByRoomCodeAndOwnerIdAndStatus(
                    request.getRoomCode(), ownerId, ContractStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active contract not found for room code: " + request.getRoomCode()));
        } else {
            throw new IllegalArgumentException("Either contractId or roomCode must be provided");
        }
        
        // Check if bill already exists for this month/year
        Optional<Bill> existingBill = billRepository.findByContract_ContractIdAndBillingMonthAndBillingYear(
                contract.getContractId(), request.getBillingMonth(), request.getBillingYear());
        if (existingBill.isPresent()) {
            throw new IllegalArgumentException(
                    "Bill already exists for this contract in " + request.getBillingMonth() + "/" + request.getBillingYear());
        }
        
        // Tạo hóa đơn và tự động lấy tất cả thông tin từ Contract
        Bill bill = new Bill();
        bill.setContract(contract);
        bill.setRoomCode(contract.getHostel().getRoomCode());
        bill.setBillingMonth(request.getBillingMonth());
        bill.setBillingYear(request.getBillingYear());
        
        // Tự động lấy giá từ Contract
        bill.setRoomPrice(contract.getMonthlyRent());
        bill.setElectricityCost(request.getElectricityCost()); // Sẽ cập nhật sau khi có chỉ số điện
        bill.setWaterCost(request.getWaterCost()); // Sẽ cập nhật sau khi có chỉ số nước
        bill.setServiceCost(contract.getServiceFee() != null ? contract.getServiceFee() : BigDecimal.ZERO);
        
        // Tính tổng tiền
        BigDecimal total = bill.getRoomPrice()
                .add(bill.getElectricityCost())
                .add(bill.getWaterCost())
                .add(bill.getServiceCost());
        bill.setTotalAmount(total);
        
        bill.setStatus(BillStatus.PENDING);
        // Hạn thanh toán mặc định: ngày 5 của tháng tiếp theo
        bill.setDueDate(LocalDate.of(request.getBillingYear(), request.getBillingMonth(), 1)
                .plusMonths(1).withDayOfMonth(5));
        bill.setNote(request.getNote());
        
        bill = billRepository.save(bill);
        log.info("Created bill {} for contract {} - Month {}/{}", 
                bill.getBillId(), contract.getContractId(), request.getBillingMonth(), request.getBillingYear());
        
        return mapToDto(bill);
    }

    @Override
    public BillResponseDTO getBillById(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + billId));
        
        // Verify access permission
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        boolean isOwner = bill.getContract().getOwner().getUser().getId().equals(userId);
        boolean isTenant = bill.getContract().getTenant() != null && 
                bill.getContract().getTenant().getUser().getId().equals(userId);
        
        if (!isOwner && !isTenant) {
            throw new RuntimeException("You don't have permission to view this bill");
        }
        
        return mapToDto(bill);
    }

    @Override
    public List<BillResponseDTO> getBillsByOwner() {
        Long ownerId = getCurrentOwnerId();
        List<Bill> bills = billRepository.findByOwnerId(ownerId);
        return bills.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<BillResponseDTO> getBillsByTenant() {
        Long tenantId = getCurrentTenantId();
        List<Bill> bills = billRepository.findByTenantId(tenantId);
        return bills.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<BillResponseDTO> getBillsByContract(Long contractId) {
        Long ownerId = getCurrentOwnerId();
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));
        
        // Verify contract belongs to current owner
        if (!contract.getOwner().getOwnerId().equals(ownerId)) {
            throw new ResourceNotFoundException("Contract not found with ID: " + contractId);
        }
        
        List<Bill> bills = billRepository.findByContract_ContractId(contractId);
        return bills.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<BillResponseDTO> getBillsByRoomCode(String roomCode) {
        Long ownerId = getCurrentOwnerId();
        List<Bill> bills = billRepository.findByRoomCodeAndOwnerId(roomCode, ownerId);
        return bills.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BillResponseDTO updateBill(Long billId, UpdateBillRequestDTO request) {
        Long ownerId = getCurrentOwnerId();
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + billId));
        
        // Verify bill belongs to current owner
        if (!bill.getContract().getOwner().getOwnerId().equals(ownerId)) {
            throw new RuntimeException("You don't have permission to update this bill");
        }
        
        // Only allow update if bill is not paid
        if (bill.getStatus() == BillStatus.PAID) {
            throw new IllegalArgumentException("Cannot update a paid bill");
        }
        
        if (request.getElectricityCost() != null) {
            bill.setElectricityCost(request.getElectricityCost());
        }
        if (request.getWaterCost() != null) {
            bill.setWaterCost(request.getWaterCost());
        }
        if (request.getServiceCost() != null) {
            bill.setServiceCost(request.getServiceCost());
        }
        if (request.getDueDate() != null) {
            bill.setDueDate(request.getDueDate());
        }
        if (request.getNote() != null) {
            bill.setNote(request.getNote());
        }
        
        // Recalculate total amount
        BigDecimal total = bill.getRoomPrice()
                .add(bill.getElectricityCost())
                .add(bill.getWaterCost())
                .add(bill.getServiceCost());
        bill.setTotalAmount(total);
        
        bill = billRepository.save(bill);
        log.info("Updated bill {}", billId);
        
        return mapToDto(bill);
    }

    @Override
    @Transactional
    public BillResponseDTO confirmPayment(Long billId, ConfirmPaymentRequestDTO request) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + billId));
        
        // Verify access permission (owner or tenant can confirm)
        Long userId = getCurrentUserId();
        boolean isOwner = bill.getContract().getOwner().getUser().getId().equals(userId);
        boolean isTenant = bill.getContract().getTenant() != null && 
                bill.getContract().getTenant().getUser().getId().equals(userId);
        
        if (!isOwner && !isTenant) {
            throw new RuntimeException("You don't have permission to confirm payment for this bill");
        }
        
        // Check if already paid
        if (bill.getStatus() == BillStatus.PAID) {
            throw new IllegalArgumentException("Bill is already paid");
        }
        
        LocalDateTime paymentTime = LocalDateTime.now();
        bill.setStatus(BillStatus.PAID);
        bill.setPaymentDate(paymentTime);
        bill.setPaymentMethod(request.getPaymentMethod());
        
        // Tự động sinh transaction code nếu user không truyền
        if (request.getTransactionCode() != null && !request.getTransactionCode().isEmpty()) {
            bill.setTransactionCode(request.getTransactionCode());
        } else {
            // Format: BILL-{billId}-{yyyyMMddHHmmss}
            String transactionCode = String.format("BILL-%d-%s", 
                billId, 
                paymentTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
            );
            bill.setTransactionCode(transactionCode);
        }
        
        if (request.getNote() != null && !request.getNote().isEmpty()) {
            bill.setNote(bill.getNote() != null ? bill.getNote() + " | " + request.getNote() : request.getNote());
        }
        
        bill = billRepository.save(bill);
        log.info("Confirmed payment for bill {} by user {}", billId, userId);
        
        // Lưu dữ liệu vào bảng payment_history
        savePaymentHistory(bill);
        
        return mapToDto(bill);
    }

    @Override
    @Transactional
    public void cancelBill(Long billId) {
        Long ownerId = getCurrentOwnerId();
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + billId));
        
        // Verify bill belongs to current owner
        if (!bill.getContract().getOwner().getOwnerId().equals(ownerId)) {
            throw new RuntimeException("You don't have permission to cancel this bill");
        }
        
        // Only allow cancel if bill is not paid
        if (bill.getStatus() == BillStatus.PAID) {
            throw new IllegalArgumentException("Cannot cancel a paid bill");
        }
        
        bill.setStatus(BillStatus.CANCELLED);
        billRepository.save(bill);
        log.info("Cancelled bill {}", billId);
    }

    @Override
    @Transactional
    public void generateMonthlyBills() {
        log.info("Starting monthly bill generation...");
        
        // Get current month and year
        YearMonth currentMonth = YearMonth.now();
        int month = currentMonth.getMonthValue();
        int year = currentMonth.getYear();
        
        // Get all active contracts
        List<Contract> activeContracts = contractRepository.findByStatus(ContractStatus.ACTIVE);
        log.info("Found {} active contracts", activeContracts.size());
        
        int createdCount = 0;
        int skippedCount = 0;
        
        for (Contract contract : activeContracts) {
            try {
                // Check if bill already exists for this month
                Optional<Bill> existingBill = billRepository.findByContract_ContractIdAndBillingMonthAndBillingYear(
                        contract.getContractId(), month, year);
                
                if (existingBill.isPresent()) {
                    log.debug("Bill already exists for contract {} - Month {}/{}", 
                            contract.getContractId(), month, year);
                    skippedCount++;
                    continue;
                }
                
                // Tạo hóa đơn tự động từ dữ liệu Contract
                Bill bill = new Bill();
                bill.setContract(contract);
                bill.setRoomCode(contract.getHostel().getRoomCode());
                bill.setBillingMonth(month);
                bill.setBillingYear(year);
                
                // Tự động lấy giá từ Contract
                bill.setRoomPrice(contract.getMonthlyRent());
                bill.setElectricityCost(BigDecimal.ZERO); // Chờ cập nhật chỉ số
                bill.setWaterCost(BigDecimal.ZERO); // Chờ cập nhật chỉ số
                bill.setServiceCost(contract.getServiceFee() != null ? contract.getServiceFee() : BigDecimal.ZERO);
                
                // Tính tổng tiền
                BigDecimal total = bill.getRoomPrice().add(bill.getServiceCost());
                bill.setTotalAmount(total);
                bill.setStatus(BillStatus.PENDING);
                
                // Hạn thanh toán: ngày 5 tháng sau
                bill.setDueDate(LocalDate.of(year, month, 1).plusMonths(1).withDayOfMonth(5));
                bill.setNote("Hóa đơn tự động tháng " + month + "/" + year);
                
                billRepository.save(bill);
                createdCount++;
                log.debug("Created bill for contract {} - Month {}/{}", 
                        contract.getContractId(), month, year);
                
            } catch (Exception e) {
                log.error("Error creating bill for contract {}: {}", contract.getContractId(), e.getMessage());
            }
        }
        
        log.info("Monthly bill generation completed. Created: {}, Skipped: {}", createdCount, skippedCount);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    private Long getCurrentOwnerId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Owner owner = ownerRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Current user is not an owner"));
        return owner.getOwnerId();
    }

    private Long getCurrentTenantId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Tenant tenant = tenantRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Current user is not a tenant"));
        return tenant.getTenantId();
    }

    @Override
    @Transactional
    public void updateOverdueBills() {
        log.info("Starting to update overdue bills");
        
        // Tìm tất cả hóa đơn chưa thanh toán và đã quá hạn
        List<Bill> overdueBills = billRepository.findOverdueBills();
        
        if (overdueBills.isEmpty()) {
            log.info("No overdue bills found");
            return;
        }
        
        log.info("Found {} overdue bills", overdueBills.size());
        
        // Cập nhật status thành OVERDUE
        for (Bill bill : overdueBills) {
            bill.setStatus(BillStatus.OVERDUE);
            billRepository.save(bill);
            log.info("Updated bill {} to OVERDUE status (Due date: {}, Room: {})", 
                    bill.getBillId(), bill.getDueDate(), bill.getRoomCode());
        }
        
        log.info("Successfully updated {} bills to OVERDUE status", overdueBills.size());
    }

    private void savePaymentHistory(Bill bill) {
        Contract contract = bill.getContract();
        Tenant tenant = contract.getTenant();
        Owner owner = contract.getOwner();
        
        PaymentHistory history = PaymentHistory.builder()
                .bill(bill)
                .billIdRef(bill.getBillId())
                .contractId(contract.getContractId())
                .roomCode(bill.getRoomCode())
                .billingMonth(bill.getBillingMonth())
                .billingYear(bill.getBillingYear())
                .roomPrice(bill.getRoomPrice())
                .electricityCost(bill.getElectricityCost())
                .waterCost(bill.getWaterCost())
                .serviceCost(bill.getServiceCost())
                .totalAmount(bill.getTotalAmount())
                .paymentMethod(bill.getPaymentMethod())
                .transactionCode(bill.getTransactionCode())
                .paymentDate(bill.getPaymentDate())
                .dueDate(bill.getDueDate())
                .tenantId(tenant != null ? tenant.getTenantId() : null)
                .tenantName(tenant != null ? tenant.getName() : null)
                .tenantPhone(tenant != null ? tenant.getPhone() : null)
                .ownerId(owner.getOwnerId())
                .ownerName(owner.getName())
                .ownerPhone(owner.getPhone())
                .note(bill.getNote())
                .build();
        
        paymentHistoryRepository.save(history);
        log.info("Saved payment history for bill {} - Room {} - Amount {}", 
                bill.getBillId(), bill.getRoomCode(), bill.getTotalAmount());
    }

    @Override
    public List<PaymentHistoryResponseDTO> getPaymentHistoryByOwner() {
        Long ownerId = getCurrentOwnerId();
        List<PaymentHistory> histories = paymentHistoryRepository.findByOwnerIdOrderByPaymentDateDesc(ownerId);
        return histories.stream().map(this::mapToPaymentHistoryDto).collect(Collectors.toList());
    }

    @Override
    public List<PaymentHistoryResponseDTO> getPaymentHistoryByTenant() {
        Long tenantId = getCurrentTenantId();
        List<PaymentHistory> histories = paymentHistoryRepository.findByTenantIdOrderByPaymentDateDesc(tenantId);
        return histories.stream().map(this::mapToPaymentHistoryDto).collect(Collectors.toList());
    }

    @Override
    public List<PaymentHistoryResponseDTO> getPaymentHistoryByRoomCode(String roomCode) {
        Long ownerId = getCurrentOwnerId();
        List<PaymentHistory> histories = paymentHistoryRepository.findByRoomCodeAndOwnerIdOrderByPaymentDateDesc(roomCode, ownerId);
        return histories.stream().map(this::mapToPaymentHistoryDto).collect(Collectors.toList());
    }

    @Override
    public List<PaymentHistoryResponseDTO> getPaymentHistoryByMonth(Integer month, Integer year) {
        Long ownerId = getCurrentOwnerId();
        List<PaymentHistory> histories = paymentHistoryRepository.findByBillingMonthAndBillingYearAndOwnerIdOrderByPaymentDateDesc(month, year, ownerId);
        return histories.stream().map(this::mapToPaymentHistoryDto).collect(Collectors.toList());
    }

    private PaymentHistoryResponseDTO mapToPaymentHistoryDto(PaymentHistory history) {
        return PaymentHistoryResponseDTO.builder()
                .paymentHistoryId(history.getPaymentHistoryId())
                .billId(history.getBillIdRef())
                .contractId(history.getContractId())
                .roomCode(history.getRoomCode())
                .billingMonth(history.getBillingMonth())
                .billingYear(history.getBillingYear())
                .roomPrice(history.getRoomPrice())
                .electricityCost(history.getElectricityCost())
                .waterCost(history.getWaterCost())
                .serviceCost(history.getServiceCost())
                .totalAmount(history.getTotalAmount())
                .paymentMethod(history.getPaymentMethod())
                .transactionCode(history.getTransactionCode())
                .paymentDate(history.getPaymentDate())
                .dueDate(history.getDueDate())
                .tenantId(history.getTenantId())
                .tenantName(history.getTenantName())
                .tenantPhone(history.getTenantPhone())
                .ownerId(history.getOwnerId())
                .ownerName(history.getOwnerName())
                .ownerPhone(history.getOwnerPhone())
                .note(history.getNote())
                .createdAt(history.getCreatedAt())
                .build();
    }

    private BillResponseDTO mapToDto(Bill bill) {
        return BillResponseDTO.builder()
                .billId(bill.getBillId())
                .contractId(bill.getContract().getContractId())
                .roomCode(bill.getRoomCode())
                .billingMonth(bill.getBillingMonth())
                .billingYear(bill.getBillingYear())
                .roomPrice(bill.getRoomPrice())
                .electricityCost(bill.getElectricityCost())
                .waterCost(bill.getWaterCost())
                .serviceCost(bill.getServiceCost())
                .totalAmount(bill.getTotalAmount())
                .status(bill.getStatus().name())
                .dueDate(bill.getDueDate())
                .paymentDate(bill.getPaymentDate())
                .note(bill.getNote())
                .paymentMethod(bill.getPaymentMethod())
                .transactionCode(bill.getTransactionCode())
                .tenantId(bill.getContract().getTenant() != null ? bill.getContract().getTenant().getTenantId() : null)
                .tenantName(bill.getContract().getTenant() != null ? bill.getContract().getTenant().getName() : null)
                .tenantPhone(bill.getContract().getTenant() != null ? bill.getContract().getTenant().getPhone() : null)
                .ownerId(bill.getContract().getOwner().getOwnerId())
                .ownerName(bill.getContract().getOwner().getName())
                .ownerPhone(bill.getContract().getOwner().getPhone())
                .createdAt(bill.getCreatedAt())
                .updatedAt(bill.getUpdatedAt())
                .build();
    }
}
