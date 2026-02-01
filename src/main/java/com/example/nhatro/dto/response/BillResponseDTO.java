package com.example.nhatro.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillResponseDTO {
    private Long billId;
    private Long contractId;
    private String roomCode;
    private Integer billingMonth;
    private Integer billingYear;
    private BigDecimal roomPrice;
    private BigDecimal electricityCost;
    private BigDecimal waterCost;
    private BigDecimal serviceCost;
    private BigDecimal totalAmount;
    private String status;
    private LocalDate dueDate;
    private LocalDateTime paymentDate;
    private String note;
    private String paymentMethod;
    private String transactionCode;
    
    // Thông tin tenant
    private Long tenantId;
    private String tenantName;
    private String tenantPhone;
    
    // Thông tin owner
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
