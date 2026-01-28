package com.example.nhatro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.config.IsAuthenticated;
import com.example.nhatro.config.IsOwner;
import com.example.nhatro.dto.request.BookingRequestDTO.BookingRequestDTO;
import com.example.nhatro.dto.request.BookingRequestDTO.BookingUpdateRequestDTO;
import com.example.nhatro.dto.response.BookingResponseDTO;
import com.example.nhatro.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Tạo booking mới và thanh toán tiền cọc
     * API: POST /api/bookings/create
     * Body: BookingRequestDTO (hostelId, checkInDate, customerName, customerPhone, customerEmail, paymentMethod)
     * Yêu cầu đăng nhập
     */
    @IsAuthenticated
    @PostMapping("/create")
    public ApiResponse<BookingResponseDTO> createBooking(@Valid @RequestBody BookingRequestDTO request) {
        try {
            BookingResponseDTO booking = bookingService.createBookingWithPayment(request);
            return ApiResponse.<BookingResponseDTO>builder()
                    .code(201)
                    .message("Booking created successfully and payment completed")
                    .result(booking)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<BookingResponseDTO>builder()
                    .code(400)
                    .message("Error creating booking: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     *  ROLE: KHÁCH HÀNG    
     * Lấy danh sách booking của user hiện tại
     * API: GET /api/bookings/my-bookings
     */
    @IsAuthenticated
    @GetMapping("/my-bookings")
    public ApiResponse<List<BookingResponseDTO>> getMyBookings() {
        try {
            List<BookingResponseDTO> bookings = bookingService.getMyBookings();
            return ApiResponse.<List<BookingResponseDTO>>builder()
                    .code(200)
                    .message("Retrieved bookings successfully")
                    .result(bookings)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<List<BookingResponseDTO>>builder()
                    .code(400)
                    .message("Error retrieving bookings: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * KHÁCH HÀNG, OWNER
     * Lấy chi tiết một booking
     * API: GET /api/bookings/{bookingId}
     */
    @IsAuthenticated
    @GetMapping("/{bookingId}")
    public ApiResponse<BookingResponseDTO> getBookingById(@PathVariable Long bookingId) {
        try {
            BookingResponseDTO booking = bookingService.getBookingById(bookingId);
            return ApiResponse.<BookingResponseDTO>builder()
                    .code(200)
                    .message("Retrieved booking successfully")
                    .result(booking)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<BookingResponseDTO>builder()
                    .code(400)
                    .message("Error retrieving booking: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * KHÁCH HÀNG, OWNER
     * Hủy booking
     * API: PUT /api/bookings/{bookingId}/cancel
     */
    @IsOwner
    @PutMapping("/{bookingId}/cancel")
    public ApiResponse<BookingResponseDTO> cancelBooking(@PathVariable Long bookingId) {
        try {
            BookingResponseDTO booking = bookingService.cancelBooking(bookingId);
            return ApiResponse.<BookingResponseDTO>builder()
                    .code(200)
                    .message("Booking cancelled successfully")
                    .result(booking)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<BookingResponseDTO>builder()
                    .code(400)
                    .message("Error cancelling booking: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
    /**
     * OWNER    
     * Owner lấy danh sách tất cả bookings của tất cả hostels
     * API: GET /api/bookings/owner/all
     */
    @IsOwner
    @GetMapping("/owner/all")
    public ApiResponse<List<BookingResponseDTO>> getAllBookingsForOwner() {
        try {
            List<BookingResponseDTO> bookings = bookingService.getAllBookingsForOwner();
            return ApiResponse.<List<BookingResponseDTO>>builder()
                    .code(200)
                    .message("Retrieved all bookings successfully")
                    .result(bookings)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<List<BookingResponseDTO>>builder()
                    .code(400)
                    .message("Error retrieving bookings: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }


    /**
     * xác nhận booking (OWNER)
     * API: PUT /api/bookings/confirm-booking/{bookingId}
     */
    @IsOwner
    @PutMapping("/confirm-booking/{bookingId}")
    public ApiResponse<BookingResponseDTO> confirmBooking(@PathVariable Long bookingId, @RequestBody @Valid BookingUpdateRequestDTO request) {
        try {
            BookingResponseDTO booking = bookingService.confirmBooking(bookingId, request.getStatus());
            return ApiResponse.<BookingResponseDTO>builder()
                    .code(200)
                    .message("Booking confirmed successfully")
                    .result(booking)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<BookingResponseDTO>builder()
                    .code(400)
                    .message("Error confirming booking: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * OWNER
     * Tìm kiếm booking theo số điện thoại khách hàng
     * API: GET /api/bookings/owner/search?phone=0123456789
     */
    @IsOwner
    @GetMapping("/owner/search")
    public ApiResponse<List<BookingResponseDTO>> searchBookingsByPhone(@RequestParam String phone) {
        try {
            List<BookingResponseDTO> bookings = bookingService.searchBookingsByPhone(phone);
            return ApiResponse.<List<BookingResponseDTO>>builder()
                    .code(200)
                    .message("Search completed successfully")
                    .result(bookings)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<List<BookingResponseDTO>>builder()
                    .code(400)
                    .message("Error searching bookings: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
}
