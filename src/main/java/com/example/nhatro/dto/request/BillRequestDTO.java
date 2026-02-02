package com.example.nhatro.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BillRequestDTO {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateBillRequestDTO {
        private Long contractId;        // Chỉ cần 1 trong 2: contractId hoặc roomCode
        private String roomCode;
        private Integer billingMonth;   
        private Integer billingYear;
        private BigDecimal electricityCost;
        private BigDecimal waterCost;
        private BigDecimal serviceCost;    
        private String note;           
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateBillRequestDTO {
        private BigDecimal electricityCost;
        private BigDecimal waterCost;
        private BigDecimal serviceCost;
        private LocalDate dueDate;
        private String note;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConfirmPaymentRequestDTO {
        private String paymentMethod; 
        private String transactionCode;
        private String note; 
    }
}
