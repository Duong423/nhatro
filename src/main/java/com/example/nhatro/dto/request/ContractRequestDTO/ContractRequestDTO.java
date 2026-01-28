package com.example.nhatro.dto.request.ContractRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractRequestDTO {
    
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDate endDate;
    
    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be positive")
    private Double monthlyRent;
    
    private Double electricityCostPerUnit; // Giá điện (VNĐ/kWh)
    
    private Double waterCostPerUnit; // Giá nước (VNĐ/m³)
    
    private Double serviceFee; // Phí dịch vụ
    
    private String paymentCycle = "MONTHLY"; // Mặc định hàng tháng
    
    private Integer numberOfTenants = 1; // Mặc định 1 người
    
    private String terms; // Điều khoản hợp đồng
    
    private String notes; // Ghi chú
}
