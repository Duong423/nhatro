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
public class PaymentHistoryResponseDTO {
    private Long paymentHistoryId;
    private Long billId;
    private Long contractId;
    private String roomCode;
    private Integer billingMonth;
    private Integer billingYear;

    // Chi tiết tiền
    private BigDecimal roomPrice;
    private BigDecimal electricityCost;
    private BigDecimal waterCost;
    private BigDecimal serviceCost;
    private BigDecimal totalAmount;

    // Thanh toán
    private String paymentMethod;
    private String transactionCode;
    private LocalDateTime paymentDate;
    private LocalDate dueDate;

    // Người thuê
    private Long tenantId;
    private String tenantName;
    private String tenantPhone;

    // Chủ nhà
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;

    private String note;
    private LocalDateTime createdAt;
}
