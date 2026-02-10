package com.example.nhatro.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentHistoryId;

    // Liên kết đến Bill gốc
    @ManyToOne
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill;

    // Thông tin hóa đơn (snapshot tại thời điểm thanh toán)
    @Column(name = "bill_id_ref", nullable = false)
    private Long billIdRef;

    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @Column(name = "room_code", length = 50)
    private String roomCode;

    @Column(name = "billing_month", nullable = false)
    private Integer billingMonth;

    @Column(name = "billing_year", nullable = false)
    private Integer billingYear;

    // Chi tiết số tiền
    @Column(name = "room_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal roomPrice;

    @Column(name = "electricity_cost", precision = 15, scale = 2)
    private BigDecimal electricityCost;

    @Column(name = "water_cost", precision = 15, scale = 2)
    private BigDecimal waterCost;

    @Column(name = "service_cost", precision = 15, scale = 2)
    private BigDecimal serviceCost;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    // Thông tin thanh toán
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "transaction_code", length = 100)
    private String transactionCode;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    // Thông tin người thuê (snapshot)
    @Column(name = "tenant_id")
    private Long tenantId;

    @Column(name = "tenant_name", length = 100)
    private String tenantName;

    @Column(name = "tenant_phone", length = 20)
    private String tenantPhone;

    // Thông tin chủ nhà (snapshot)
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "owner_phone", length = 20)
    private String ownerPhone;

    // Ghi chú
    @Column(name = "note", length = 500)
    private String note;
}
