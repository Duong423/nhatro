package com.example.nhatro.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // Tìm hợp đồng active theo roomCode và ownerId (an toàn hơn)
    Optional<Contract> findByHostel_RoomCodeAndHostel_Owner_IdAndStatus(String roomCode, Long userId, ContractStatus status);
    
    // Tìm hợp đồng active theo roomCode và owner.ownerId (từ Contract.owner, không phải hostel.owner)
    @Query("SELECT c FROM Contract c WHERE c.hostel.roomCode = :roomCode AND c.owner.ownerId = :ownerId AND c.status = :status")
    Optional<Contract> findByRoomCodeAndOwnerIdAndStatus(@Param("roomCode") String roomCode, @Param("ownerId") Long ownerId, @Param("status") ContractStatus status);

    // Tìm hợp đồng theo trạng thái
    List<Contract> findByStatus(ContractStatus status);
    
    // Tìm hợp đồng đang active của một tenant (bằng tenant.tenantId)
    Optional<Contract> findByTenant_TenantIdAndStatus(Long tenantId, ContractStatus status);
    
    // Tìm các hợp đồng ACTIVE đã hết hạn (endDate < today)
    List<Contract> findByStatusAndEndDateBefore(ContractStatus status, LocalDate date);
    
    // Tìm hợp đồng theo số điện thoại tenant (cho owner)
    @Query("SELECT c FROM Contract c WHERE c.owner.ownerId = :ownerId AND c.tenant.phone LIKE %:phone%")
    List<Contract> findByOwnerIdAndPhone(@Param("ownerId") Long ownerId, @Param("phone") String phone);

    // Tìm danh sách hợp đồng của owner theo trạng thái
    List<Contract> findByOwner_OwnerIdAndStatus(Long ownerId, ContractStatus status);
}
