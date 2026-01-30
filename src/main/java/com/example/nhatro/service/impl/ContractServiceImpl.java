package com.example.nhatro.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nhatro.dto.request.ContractRequestDTO.ContractRequestDTO;
import com.example.nhatro.dto.request.ContractRequestDTO.UpdateContractRequestDTO;
import com.example.nhatro.dto.response.ContractResponseDTO;
import com.example.nhatro.entity.Booking;
import com.example.nhatro.entity.Contract;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.entity.User;
import com.example.nhatro.enums.BookingStatus;
import com.example.nhatro.enums.ContractStatus;
import com.example.nhatro.enums.HostelStatus;
import com.example.nhatro.repository.BookingRepository;
import com.example.nhatro.repository.ContractRepository;
import com.example.nhatro.repository.HostelRepository;
import com.example.nhatro.repository.UserRepository;
import com.example.nhatro.service.ContractService;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HostelRepository hostelRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public ContractResponseDTO createContractFromBooking(ContractRequestDTO request) {
        // Kiểm tra booking có tồn tại và đã được confirmed
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + request.getBookingId()));

        // Kiểm tra booking phải có customer (không phải khách vãng lai)
        if (booking.getCustomer() == null) {
            throw new RuntimeException("Cannot create contract for guest booking. Customer must register an account first.");
        }

        // Kiểm tra booking phải ở trạng thái CONFIRMED
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Booking must be CONFIRMED to create contract. Current status: " + booking.getStatus());
        }

        // Kiểm tra đã có hợp đồng cho booking này chưa
        if (contractRepository.findByBookingBookingId(request.getBookingId()).isPresent()) {
            throw new RuntimeException("Contract already exists for this booking");
        }

        // Lấy current user (phải là owner của hostel)
        User currentUser = getCurrentUser();
        if (!booking.getHostel().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only hostel owner can create contract");
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }

        // Tạo hợp đồng
        Contract contract = new Contract();
        contract.setBooking(booking);
        contract.setTenant(booking.getCustomer()); // Khách thuê là customer trong booking
        contract.setTenantName(request.getTenantName()); // Lấy tên từ request
        contract.setTenantPhone(request.getPhoneNumberTenant()); // Lấy SĐT từ request
        contract.setTenantEmail(request.getTenantEmail()); // Lấy email từ request
        contract.setOwner(booking.getHostel().getOwner()); // Chủ nhà là owner của hostel
        contract.setOwnerName(request.getOwnerName()); // Lấy tên từ request
        contract.setOwnerPhone(request.getPhoneNumberOwner()); // Lấy SĐT từ request
        contract.setHostel(booking.getHostel());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setMonthlyRent(BigDecimal.valueOf(request.getMonthlyRent()));
        contract.setDepositAmount(booking.getDepositAmount()); // Lấy từ booking
        
        // Set optional fields
        if (request.getElectricityCostPerUnit() != null) {
            contract.setElectricityCostPerUnit(BigDecimal.valueOf(request.getElectricityCostPerUnit()));
        }
        if (request.getWaterCostPerUnit() != null) {
            contract.setWaterCostPerUnit(BigDecimal.valueOf(request.getWaterCostPerUnit()));
        }
        if (request.getServiceFee() != null) {
            contract.setServiceFee(BigDecimal.valueOf(request.getServiceFee()));
        }
        
        contract.setPaymentCycle(request.getPaymentCycle());
        contract.setNumberOfTenants(request.getNumberOfTenants());
        contract.setTerms(request.getTerms());
        contract.setStatus(ContractStatus.PENDING); // Mặc định là PENDING, chờ ký
        contract.setNotes(request.getNotes());

        contract = contractRepository.save(contract);

        return mapToContractResponse(contract);
    }

    @Override
    public ContractResponseDTO getContractById(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new com.example.nhatro.exception.ResourceNotFoundException("Contract not found with ID: " + contractId));
        
        // Kiểm tra quyền: chỉ tenant hoặc landlord mới xem được
        User currentUser = getCurrentUser();
        if (!contract.getTenant().getId().equals(currentUser.getId()) && 
            !contract.getOwner().getId().equals(currentUser.getId())) {
            throw new com.example.nhatro.exception.PermissionDeniedException("You don't have permission to view this contract");
        }
        
        return mapToContractResponse(contract);
    }

    @Override
    public ContractResponseDTO getContractByBookingId(Long bookingId) {
        Contract contract = contractRepository.findByBookingBookingId(bookingId)
                .orElseThrow(() -> new com.example.nhatro.exception.ResourceNotFoundException("Contract not found for booking ID: " + bookingId));
        
        return mapToContractResponse(contract);
    }

    @Override
    public List<ContractResponseDTO> getContractsByTenant() {
        User currentUser = getCurrentUser();
        List<Contract> contracts = contractRepository.findByTenantId(currentUser.getId());
        
        return contracts.stream()
                .map(this::mapToContractResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContractResponseDTO> getContractsByOwner() {
        User currentUser = getCurrentUser();
        List<Contract> contracts = contractRepository.findByOwnerId(currentUser.getId());
        
        return contracts.stream()
                .map(this::mapToContractResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContractResponseDTO signContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + contractId));
        
        // Kiểm tra trạng thái
        if (contract.getStatus() != ContractStatus.PENDING) {
            throw new RuntimeException("Only PENDING contracts can be signed");
        }
        
        // Kiểm tra quyền: chỉ tenant hoặc landlord mới ký được
        User currentUser = getCurrentUser();
        if (!contract.getTenant().getId().equals(currentUser.getId()) && 
            !contract.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to sign this contract");
        }
        
        // Chuyển sang ACTIVE và set ngày ký
        contract.setStatus(ContractStatus.ACTIVE);
        contract.setSignedDate(LocalDate.now());
        contract = contractRepository.save(contract);
        
        // Cập nhật booking status sang COMPLETED khi hợp đồng được ký
        Booking booking = contract.getBooking();
        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);
        
        return mapToContractResponse(contract);
    }

    @Override
    @Transactional
    public ContractResponseDTO terminateContract(Long contractId, String reason) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + contractId));
        
        // Kiểm tra trạng thái
        if (contract.getStatus() == ContractStatus.TERMINATED) {
            throw new RuntimeException("Contract is already terminated");
        }
        
        // Kiểm tra quyền: chỉ owner mới chấm dứt được
        User currentUser = getCurrentUser();
        if (!contract.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only owner can terminate contract");
        }
        
        // Chấm dứt hợp đồng
        contract.setStatus(ContractStatus.TERMINATED);
        contract.setNotes((contract.getNotes() != null ? contract.getNotes() + "\n" : "") + 
                         "Terminated: " + reason);
        contract = contractRepository.save(contract);
        
        // Cập nhật hostel status sang AVAILABLE khi hợp đồng bị chấm dứt
        Hostel hostel = contract.getHostel();
        hostel.setStatus(HostelStatus.AVAILABLE);
        hostelRepository.save(hostel);
        
        return mapToContractResponse(contract);
    }

    @Override
    @Transactional
    public ContractResponseDTO updateContract(Long contractId, UpdateContractRequestDTO request) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found with ID: " + contractId));
        
        // Chỉ cho phép owner cập nhật
        User currentUser = getCurrentUser();
        if (!contract.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only owner can update contract");
        }
        
        // Không cho phép cập nhật hợp đồng đã TERMINATED
        if (contract.getStatus() == ContractStatus.TERMINATED) {
            throw new RuntimeException("Cannot update terminated contract");
        }
        
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new RuntimeException("End date must be after start date");
        }
        
        // Cập nhật thông tin
        contract.setTenantName(request.getTenantName());
        contract.setTenantEmail(request.getTenantEmail());
        contract.setTenantPhone(request.getPhoneNumberTenant());
        contract.setOwnerName(request.getOwnerName());
        contract.setOwnerPhone(request.getPhoneNumberOwner());
        contract.setStartDate(request.getStartDate());
        contract.setEndDate(request.getEndDate());
        contract.setMonthlyRent(BigDecimal.valueOf(request.getMonthlyRent()));
        
        if (request.getDepositAmount() != null) {
            contract.setDepositAmount(BigDecimal.valueOf(request.getDepositAmount()));
        }
        if (request.getElectricityCostPerUnit() != null) {
            contract.setElectricityCostPerUnit(BigDecimal.valueOf(request.getElectricityCostPerUnit()));
        }
        if (request.getWaterCostPerUnit() != null) {
            contract.setWaterCostPerUnit(BigDecimal.valueOf(request.getWaterCostPerUnit()));
        }
        if (request.getServiceFee() != null) {
            contract.setServiceFee(BigDecimal.valueOf(request.getServiceFee()));
        }
        
        contract.setPaymentCycle(request.getPaymentCycle());
        contract.setNumberOfTenants(request.getNumberOfTenants());
        contract.setTerms(request.getTerms());
        contract.setNotes(request.getNotes());
        
        contract = contractRepository.save(contract);
        
        return mapToContractResponse(contract);
    }

    private ContractResponseDTO mapToContractResponse(Contract contract) {
        // Lấy thông tin khách hàng từ booking (đã lưu khi tạo booking)
        Booking booking = contract.getBooking();
        
        return ContractResponseDTO.builder()
                .contractId(contract.getContractId())
                .bookingId(booking.getBookingId())
                .tenantId(contract.getTenant().getId())
                .tenantName(contract.getTenantName())
                .phoneNumberTenant(contract.getTenantPhone())
                .tenantEmail(contract.getTenantEmail())
                .ownerId(contract.getOwner().getId())
                .ownerName(contract.getOwnerName())
                .phoneNumberOwner(contract.getOwnerPhone())
                .hostelId(contract.getHostel().getHostelId())
                .hostelName(contract.getHostel().getName())
                .hostelAddress(contract.getHostel().getAddress())
                .hostelPrice(contract.getHostel().getPrice())
                .hostelArea(contract.getHostel().getArea())
                .hostelAmenities(contract.getHostel().getAmenities())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .monthlyRent(contract.getMonthlyRent())
                .depositAmount(contract.getDepositAmount())
                .electricityCostPerUnit(contract.getElectricityCostPerUnit())
                .waterCostPerUnit(contract.getWaterCostPerUnit())
                .serviceFee(contract.getServiceFee())
                .paymentCycle(contract.getPaymentCycle())
                .numberOfTenants(contract.getNumberOfTenants())
                .terms(contract.getTerms())
                .signedDate(contract.getSignedDate())
                .status(contract.getStatus().name())
                .notes(contract.getNotes())
                .createdAt(contract.getCreatedAt())
                .build();
    }
}
