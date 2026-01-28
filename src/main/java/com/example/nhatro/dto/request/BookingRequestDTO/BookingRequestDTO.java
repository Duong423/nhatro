package com.example.nhatro.dto.request.BookingRequestDTO;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    
    @NotNull(message = "Hostel ID is required")
    private Long hostelId;
    
    @NotNull(message = "Check-in date is required")
    @Future(message = "Check-in date must be in the future")
    private LocalDateTime checkInDate;
    
    // Optional: nếu không nhập sẽ lấy từ token của user đang login
    private String customerName;
    
    // Optional: nếu không nhập sẽ lấy từ token của user đang login
    private String customerPhone;
    
    // Optional: nếu không nhập sẽ lấy từ token của user đang login
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    private String notes;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, BANKING, MOMO, VNPAY
}
