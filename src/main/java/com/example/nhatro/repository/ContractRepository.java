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
    
    // Tìm tất cả hợp đồng của một tenant (bằng tenant.tenantId)
    List<Contract> findByTenant_TenantId(Long tenantId);

    // Tìm tất cả hợp đồng của một landlord (owner.ownerId)
    List<Contract> findByOwner_OwnerId(Long ownerId);

    // Hỗ trợ tìm bằng user id (user liên kết thông qua tenant.user hoặc owner.user)
    // Tìm hợp đồng theo user.id thông qua relation tenant.user.id hoặc owner.user.id
    List<Contract> findByTenant_User_Id(Long userId);
    List<Contract> findByOwner_User_Id(Long userId);
    
    // Tìm tất cả hợp đồng của một hostel
    List<Contract> findByHostelHostelId(Long hostelId);
    
    // Tìm hợp đồng active theo roomCode
    Optional<Contract> findByHostel_RoomCodeAndStatus(String roomCode, ContractStatus status);

    // Tìm hợp đồng theo trạng thái
    List<Contract> findByStatus(ContractStatus status);
    
    // Tìm hợp đồng đang active của một tenant (bằng tenant.tenantId)
    Optional<Contract> findByTenant_TenantIdAndStatus(Long tenantId, ContractStatus status);
}
