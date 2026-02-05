package com.example.nhatro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.nhatro.entity.Bill;
import com.example.nhatro.enums.BillStatus;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    
    // Tìm hóa đơn theo contract
    List<Bill> findByContract_ContractId(Long contractId);
    
    // Tìm hóa đơn theo roomCode và owner (chỉ lấy của hợp đồng ACTIVE)
    @Query("SELECT b FROM Bill b WHERE b.roomCode = :roomCode AND b.contract.owner.ownerId = :ownerId AND b.contract.status = 'ACTIVE'")
    List<Bill> findByRoomCodeAndOwnerId(@Param("roomCode") String roomCode, @Param("ownerId") Long ownerId);
    
    // Tìm hóa đơn theo tháng/năm và contract
    Optional<Bill> findByContract_ContractIdAndBillingMonthAndBillingYear(
            Long contractId, Integer month, Integer year);
    
    // Tìm hóa đơn theo status
    List<Bill> findByStatus(BillStatus status);
    
    // Tìm hóa đơn của owner (chỉ lấy của hợp đồng ACTIVE)
    @Query("SELECT b FROM Bill b WHERE b.contract.owner.ownerId = :ownerId AND b.contract.status = 'ACTIVE' ORDER BY b.billingYear DESC, b.billingMonth DESC")
    List<Bill> findByOwnerId(@Param("ownerId") Long ownerId);
    
    // Tìm hóa đơn của tenant (chỉ lấy của hợp đồng ACTIVE)
    @Query("SELECT b FROM Bill b WHERE b.contract.tenant.tenantId = :tenantId AND b.contract.status = 'ACTIVE' ORDER BY b.billingYear DESC, b.billingMonth DESC")
    List<Bill> findByTenantId(@Param("tenantId") Long tenantId);
    
    // Tìm các hóa đơn của các contract đang active
    @Query("SELECT b FROM Bill b WHERE b.contract.status = 'ACTIVE'")
    List<Bill> findAllActiveContractBills();
    
    // Tìm hóa đơn quá hạn: status = PENDING và dueDate < ngày hiện tại
    @Query("SELECT b FROM Bill b WHERE b.status = 'PENDING' AND b.dueDate < CURRENT_DATE")
    List<Bill> findOverdueBills();
}
