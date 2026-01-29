package com.example.nhatro.dto.request.ContractRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.aspectj.weaver.ast.Not;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "Owner name is required")
    private String ownerName;

    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải đủ 10 chữ số")
    private String phoneNumberOwner;

    @NotNull(message = "Tenant name is required")
    private String tenantName;

    @NotNull(message = "Tenant email is required")
    @Email(message = "Invalid email format")
    private String tenantEmail;
    
    @NotNull(message = "Phone number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Số điện thoại phải đủ 10 chữ số")
    private String phoneNumberTenant;

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
