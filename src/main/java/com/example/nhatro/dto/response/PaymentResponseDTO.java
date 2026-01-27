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
public class PaymentResponseDTO {
    
    private Long paymentId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private String note;
    private LocalDateTime paidAt;
}
