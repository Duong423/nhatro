package com.example.nhatro.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.nhatro.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @OneToOne
    @JoinColumn(name = "booking_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "bookingId")
    @JsonIdentityReference(alwaysAsId = true)
    private Booking booking; // Liên kết với booking

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "invoiceId")
    @JsonIdentityReference(alwaysAsId = true)
    private Invoice invoice; // Giữ lại cho các payment hóa đơn hàng tháng

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(length = 50)
    private String paymentMethod; // CASH, BANKING, MOMO, VNPAY
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String note;
    
    @Column(length = 100)
    private String transactionId; // Mã giao dịch từ cổng thanh toán
    
    private LocalDateTime paidAt;
}