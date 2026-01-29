package com.example.nhatro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nhatro.entity.Contract;
import com.example.nhatro.enums.ContractStatus;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    
    // Tìm hợp đồng theo booking ID
    Optional<Contract> findByBookingBookingId(Long bookingId);
    
    // Tìm tất cả hợp đồng của một tenant
    List<Contract> findByTenantId(Long tenantId);
    
    // Tìm tất cả hợp đồng của một landlord (owner)
    List<Contract> findByOwnerId(Long ownerId);
    
    // Tìm tất cả hợp đồng của một hostel
    List<Contract> findByHostelHostelId(Long hostelId);
    
    // Tìm hợp đồng theo trạng thái
    List<Contract> findByStatus(ContractStatus status);
    
    // Tìm hợp đồng đang active của một tenant
    Optional<Contract> findByTenantIdAndStatus(Long tenantId, ContractStatus status);
}
