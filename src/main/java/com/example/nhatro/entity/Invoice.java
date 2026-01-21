package com.example.nhatro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.nhatro.enums.InvoiceStatus;
import com.example.nhatro.entity.InvoiceDetail;

@Entity
@Table(name = "invoices")
@Data
@NoArgsConstructor

@EqualsAndHashCode(callSuper = true)
public class Invoice extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;

    @ManyToOne
    @JoinColumn(name = "contract_id")
    private Contract contract;

    private LocalDate monthYear; // VD: 2023-10-01 (Hóa đơn tháng 10)
    
    // Các cột tổng hợp nhanh
    private BigDecimal totalAmount;
    private BigDecimal amountRoom;
    private BigDecimal amountService;
    private BigDecimal amountElec;
    private BigDecimal amountWater;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;
    private LocalDate dueDate; // Hạn chót

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<InvoiceDetail> details;
}