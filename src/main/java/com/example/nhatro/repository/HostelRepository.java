package com.example.nhatro.repository;

import com.example.nhatro.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.nhatro.enums.HostelStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {
    // You can add custom query methods here if needed
    
    @Query("SELECT h FROM Hostel h WHERE h.hostelId = :hostelId")
    Optional<Hostel> findByIdWithServices(@Param("hostelId") Long hostelId);
    
    // Lấy danh sách hostel của một owner cụ thể
    @Query("SELECT h FROM Hostel h WHERE h.owner.id = :ownerId")
    List<Hostel> findByOwnerId(@Param("ownerId") Long ownerId);

    // Đếm tổng số phòng của owner
    @Query("SELECT COUNT(h) FROM Hostel h WHERE h.owner.id = :ownerId")
    long countByOwnerId(@Param("ownerId") Long ownerId);

    // Đếm số phòng còn trống (AVAILABLE) của owner
    @Query("SELECT COUNT(h) FROM Hostel h WHERE h.owner.id = :ownerId AND h.status = :status")
    long countByOwnerIdAndStatus(@Param("ownerId") Long ownerId, @Param("status") HostelStatus status);

    // Đếm tổng số phòng trong hệ thống
    @Query("SELECT COUNT(h) FROM Hostel h")
    long countAllRooms();

    // Đếm số phòng còn trống (AVAILABLE) trong hệ thống
    @Query("SELECT COUNT(h) FROM Hostel h WHERE h.status = :status")
    long countByStatus(@Param("status") HostelStatus status);
}
