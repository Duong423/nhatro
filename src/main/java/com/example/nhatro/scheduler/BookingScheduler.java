package com.example.nhatro.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.nhatro.entity.Booking;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.enums.BookingStatus;
import com.example.nhatro.enums.HostelStatus;
import com.example.nhatro.repository.BookingRepository;
import com.example.nhatro.repository.HostelRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled task để tự động hủy các booking PENDING quá hạn (5 ngày)
 * và cập nhật hostel status về AVAILABLE
 */
@Component
@Slf4j
public class BookingScheduler {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HostelRepository hostelRepository;

    /**
     * Chạy mỗi giờ để kiểm tra và hủy booking quá hạn
     * Cron format: second minute hour day month weekday
     * 0 0 * * * * = Chạy vào đầu mỗi giờ
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cancelExpiredBookings() {
        log.info("Starting scheduled task to cancel expired bookings...");

        // Tính thời gian 5 ngày trước
        LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);

        // Tìm tất cả booking PENDING đã quá 5 ngày
        List<Booking> expiredBookings = bookingRepository.findByStatusAndCreatedAtBefore(
                BookingStatus.PENDING, 
                fiveDaysAgo
        );

        if (expiredBookings.isEmpty()) {
            log.info("No expired bookings found.");
            return;
        }

        log.info("Found {} expired booking(s) to cancel.", expiredBookings.size());

        int cancelledCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                // Cập nhật booking status thành CANCELLED
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);

                // Cập nhật hostel status về AVAILABLE nếu đang RESERVED
                Hostel hostel = booking.getHostel();
                if (hostel.getStatus() == HostelStatus.RESERVED) {
                    hostel.setStatus(HostelStatus.AVAILABLE);
                    hostelRepository.save(hostel);
                    log.info("Cancelled booking ID: {} and released hostel ID: {} to AVAILABLE", 
                            booking.getBookingId(), hostel.getHostelId());
                } else {
                    log.info("Cancelled booking ID: {}, hostel ID: {} status is {}", 
                            booking.getBookingId(), hostel.getHostelId(), hostel.getStatus());
                }

                cancelledCount++;
            } catch (Exception e) {
                log.error("Error cancelling booking ID: {}", booking.getBookingId(), e);
            }
        }

        log.info("Completed: Cancelled {} out of {} expired booking(s).", 
                cancelledCount, expiredBookings.size());
    }
}
