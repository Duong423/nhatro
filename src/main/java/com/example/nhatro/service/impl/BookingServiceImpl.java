package com.example.nhatro.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nhatro.dto.request.BookingRequestDTO;
import com.example.nhatro.dto.response.BookingResponseDTO;
import com.example.nhatro.dto.response.PaymentResponseDTO;
import com.example.nhatro.entity.Booking;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.entity.Payment;
import com.example.nhatro.entity.User;
import com.example.nhatro.enums.BookingStatus;
import com.example.nhatro.enums.HostelStatus;
import com.example.nhatro.enums.PaymentStatus;
import com.example.nhatro.repository.BookingRepository;
import com.example.nhatro.repository.HostelRepository;
import com.example.nhatro.repository.PaymentRepository;
import com.example.nhatro.repository.UserRepository;
import com.example.nhatro.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private HostelRepository hostelRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Tạo booking mới và xử lý thanh toán tiền cọc
     * Flow: 
     * 1. Kiểm tra hostel có tồn tại và còn phòng không
     * 2. Tạo booking với status PENDING
     * 3. Tạo payment với depositAmount từ hostel
     * 4. Cập nhật booking status thành CONFIRMED
     * 5. Cập nhật hostel status thành FULL
     * 6. Trả về thông tin booking và payment
     */
    @Override
    @Transactional
    public BookingResponseDTO createBookingWithPayment(BookingRequestDTO request) {
        // Lấy user hiện tại
        User currentUser = getCurrentUser();
        
        // 1. Kiểm tra hostel
        Hostel hostel = hostelRepository.findById(request.getHostelId())
                .orElseThrow(() -> new RuntimeException("Hostel not found with ID: " + request.getHostelId()));
        
        // Kiểm tra hostel còn phòng không
        if (hostel.getStatus() == HostelStatus.FULL) {
            throw new RuntimeException("Hostel is already full");
        }
        
        if (hostel.getStatus() == HostelStatus.CLOSED) {
            throw new RuntimeException("Hostel is closed");
        }
        
        if (hostel.getStatus() == HostelStatus.UNDER_RENOVATION) {
            throw new RuntimeException("Hostel is under renovation");
        }
        
        // Kiểm tra có tiền cọc không
        if (hostel.getDepositAmount() == null || hostel.getDepositAmount().doubleValue() <= 0) {
            throw new RuntimeException("Deposit amount is not set for this hostel");
        }
        
        // 2. Tạo booking
        Booking booking = new Booking();
        booking.setCustomer(currentUser);
        booking.setHostel(hostel);
        booking.setBookingDate(LocalDateTime.now());
        booking.setCheckInDate(request.getCheckInDate());
        booking.setDepositAmount(hostel.getDepositAmount());
        booking.setStatus(BookingStatus.PENDING);
        booking.setNotes(request.getNotes());
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setCustomerEmail(request.getCustomerEmail());
        
        booking = bookingRepository.save(booking);
        
        // 3. Tạo payment cho tiền cọc
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(hostel.getDepositAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.COMPLETED); // Giả sử thanh toán thành công
        payment.setPaidAt(LocalDateTime.now());
        payment.setTransactionId("TXN-" + System.currentTimeMillis());
        payment.setNote("Deposit payment for booking #" + booking.getBookingId());
        
        payment = paymentRepository.save(payment);
        
        // 4. Cập nhật booking status thành CONFIRMED
        booking.setStatus(BookingStatus.CONFIRMED);
        booking = bookingRepository.save(booking);
        
        // 5. Cập nhật hostel status thành FULL
        hostel.setStatus(HostelStatus.FULL);
        hostelRepository.save(hostel);
        
        // 6. Trả về response
        return mapToBookingResponse(booking, payment);
    }

    @Override
    public List<BookingResponseDTO> getMyBookings() {
        User currentUser = getCurrentUser();
        List<Booking> bookings = bookingRepository.findByCustomerId(currentUser.getId());
        
        return bookings.stream()
                .map(booking -> {
                    Payment payment = paymentRepository.findByBookingBookingId(booking.getBookingId())
                            .orElse(null);
                    return mapToBookingResponse(booking, payment);
                })
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDTO getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
        
        // Kiểm tra quyền truy cập
        User currentUser = getCurrentUser();
        if (!booking.getCustomer().getId().equals(currentUser.getId()) 
                && !booking.getHostel().getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to access this booking");
        }
        
        Payment payment = paymentRepository.findByBookingBookingId(bookingId).orElse(null);
        return mapToBookingResponse(booking, payment);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with ID: " + bookingId));
        
        // Kiểm tra quyền
        User currentUser = getCurrentUser();
        if (!booking.getCustomer().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to cancel this booking");
        }
        
        // Chỉ có thể hủy booking ở trạng thái PENDING hoặc CONFIRMED
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Cannot cancel booking with status: " + booking.getStatus());
        }
        
        // Cập nhật status
        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        
        // Cập nhật lại hostel status về AVAILABLE
        Hostel hostel = booking.getHostel();
        hostel.setStatus(HostelStatus.AVAILABLE);
        hostelRepository.save(hostel);
        
        Payment payment = paymentRepository.findByBookingBookingId(bookingId).orElse(null);
        return mapToBookingResponse(booking, payment);
    }

    @Override
    public BookingResponseDTO getBookingByHostel(Long hostelId) {
        // Kiểm tra hostel tồn tại
        Hostel hostel = hostelRepository.findById(hostelId)
                .orElseThrow(() -> new RuntimeException("Hostel not found with ID: " + hostelId));
        
        // Kiểm tra quyền (chỉ owner mới xem được)
        User currentUser = getCurrentUser();
        if (!hostel.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to view booking for this hostel");
        }
        
        Booking booking = bookingRepository.findByHostelHostelId(hostelId);
        
        if (booking == null) {
            throw new RuntimeException("No booking found for this hostel");
        }
        
        Payment payment = paymentRepository.findByBookingBookingId(booking.getBookingId())
                .orElse(null);
        return mapToBookingResponse(booking, payment);
    }

    @Override
    public List<BookingResponseDTO> getAllBookingsForOwner() {
        User currentUser = getCurrentUser();
        
        // Lấy tất cả hostels của owner
        List<Hostel> hostels = hostelRepository.findByOwnerId(currentUser.getId());
        
        if (hostels.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Lấy danh sách hostel IDs
        List<Long> hostelIds = hostels.stream()
                .map(Hostel::getHostelId)
                .collect(Collectors.toList());
        
        // Lấy tất cả bookings theo hostel IDs
        List<Booking> bookings = bookingRepository.findByHostelHostelIdIn(hostelIds);
        
        return bookings.stream()
                .map(booking -> {
                    Payment payment = paymentRepository.findByBookingBookingId(booking.getBookingId())
                            .orElse(null);
                    return mapToBookingResponse(booking, payment);
                })
                .collect(Collectors.toList());
    }

    // Helper methods
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private BookingResponseDTO mapToBookingResponse(Booking booking, Payment payment) {
        PaymentResponseDTO paymentDTO = null;
        if (payment != null) {
            paymentDTO = PaymentResponseDTO.builder()
                    .paymentId(payment.getPaymentId())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod())
                    .status(payment.getStatus().name())
                    .transactionId(payment.getTransactionId())
                    .note(payment.getNote())
                    .paidAt(payment.getPaidAt())
                    .build();
        }
        
        return BookingResponseDTO.builder()
                .bookingId(booking.getBookingId())
                .customerId(booking.getCustomer().getId())
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .customerEmail(booking.getCustomerEmail())
                .hostelId(booking.getHostel().getHostelId())
                .hostelName(booking.getHostel().getName())
                .hostelAddress(booking.getHostel().getAddress())
                .bookingDate(booking.getBookingDate())
                .checkInDate(booking.getCheckInDate())
                .depositAmount(booking.getDepositAmount())
                .status(booking.getStatus().name())
                .notes(booking.getNotes())
                .payment(paymentDTO)
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
