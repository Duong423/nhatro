package com.example.nhatro.dto.request.ContractRequestDTO;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateContractRequestDTO {
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @NotNull(message = "Monthly rent is required")
    @Min(value = 0, message = "Monthly rent must be greater than or equal to 0")
    private Double monthlyRent;
    
    @Min(value = 0, message = "Deposit amount must be greater than or equal to 0")
    private Double depositAmount;
    
    @Min(value = 0, message = "Electricity cost must be greater than or equal to 0")
    private Double electricityCostPerUnit;
    
    @Min(value = 0, message = "Water cost must be greater than or equal to 0")
    private Double waterCostPerUnit;
    
    @Min(value = 0, message = "Service fee must be greater than or equal to 0")
    private Double serviceFee;
    
    @NotNull(message = "Payment cycle is required")
    private String paymentCycle;
    
    @NotNull(message = "Number of tenants is required")
    @Min(value = 1, message = "Number of tenants must be at least 1")
    private Integer numberOfTenants;
    
    @NotNull(message = "Terms is required")
    private String terms;
    
    private String notes;
}
