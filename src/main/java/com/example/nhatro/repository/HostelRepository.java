package com.example.nhatro.repository;

import com.example.nhatro.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostelRepository extends JpaRepository<Hostel, Long> {
    // You can add custom query methods here if needed
    
    @Query("SELECT h FROM Hostel h LEFT JOIN FETCH h.services WHERE h.hostelId = :hostelId")
    Optional<Hostel> findByIdWithServices(@Param("hostelId") Long hostelId);
    
    // Lấy danh sách hostel của một owner cụ thể
    @Query("SELECT h FROM Hostel h WHERE h.owner.id = :ownerId")
    List<Hostel> findByOwnerId(@Param("ownerId") Long ownerId);
}
