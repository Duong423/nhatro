package com.example.nhatro.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    
    private Long bookingId;
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    
    private Long hostelId;
    private String hostelName;
    private String hostelAddress;
    private String roomCode;
    
    private LocalDateTime bookingDate;
    private LocalDateTime checkInDate;
    
    private BigDecimal depositAmount;
    private String status; // Trạng thái booking
    
    private String notes;
    
    private PaymentResponseDTO payment; // Thông tin thanh toán
    
    private LocalDateTime createdAt;
}
