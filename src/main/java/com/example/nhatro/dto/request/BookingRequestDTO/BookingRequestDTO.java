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
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @NotBlank(message = "Customer phone is required")
    private String customerPhone;
    
    @Email(message = "Invalid email format")
    private String customerEmail;
    
    private String notes;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, BANKING, MOMO, VNPAY
}
