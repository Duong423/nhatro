package com.example.nhatro.service;

import java.util.List;

import com.example.nhatro.dto.request.BookingRequestDTO;
import com.example.nhatro.dto.response.BookingResponseDTO;

public interface BookingService {
    
    /**
     * Tạo booking mới và xử lý thanh toán tiền cọc
     * @param request thông tin đặt phòng
     * @return thông tin booking và payment đã tạo
     */
    BookingResponseDTO createBookingWithPayment(BookingRequestDTO request);
    
    /**
     * Lấy danh sách booking của user hiện tại
     * @return danh sách booking
     */
    List<BookingResponseDTO> getMyBookings();
    
    /**
     * Lấy chi tiết một booking
     * @param bookingId ID của booking
     * @return thông tin booking
     */
    BookingResponseDTO getBookingById(Long bookingId);
    
    /**
     * Hủy booking
     * @param bookingId ID của booking
     * @return thông tin booking đã hủy
     */
    BookingResponseDTO cancelBooking(Long bookingId);
    
    /**
     * Lấy booking theo hostel (cho owner)
     * @param hostelId ID của hostel
     * @return thông tin booking
     */
    BookingResponseDTO getBookingByHostel(Long hostelId);
    
    /**
     * Lấy tất cả bookings của owner (tất cả hostels của owner)
     * @return danh sách tất cả booking
     */
    List<BookingResponseDTO> getAllBookingsForOwner();
}
