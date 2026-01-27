package com.example.nhatro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.nhatro.entity.Booking;
import com.example.nhatro.enums.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    // Tìm booking theo customer
    List<Booking> findByCustomerId(Long customerId);
    
    // Tìm booking theo hostel (1-1 relationship)
    Booking findByHostelHostelId(Long hostelId);
    
    // Tìm tất cả bookings theo danh sách hostel IDs
    List<Booking> findByHostelHostelIdIn(List<Long> hostelIds);
    
    // Tìm booking theo status
    List<Booking> findByStatus(BookingStatus status);
    
    // Tìm booking theo customer và status
    List<Booking> findByCustomerIdAndStatus(Long customerId, BookingStatus status);
}
