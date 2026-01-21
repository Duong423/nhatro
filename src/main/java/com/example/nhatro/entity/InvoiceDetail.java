package com.example.nhatro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_details")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    // Lưu cứng dữ liệu
    private String serviceName; // "Tiền điện (Mới: 200 - Cũ: 100)"
    private BigDecimal unitPrice; 
    private BigDecimal quantity; 
    private BigDecimal amount; // = unitPrice * quantity

    private String type; // ROOM | ELECTRIC | WATER | SERVICE
}