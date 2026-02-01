package com.example.nhatro.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.nhatro.enums.BillStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;
    
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;
    
    @Column(name = "room_code", length = 50)
    private String roomCode;
    
    @Column(name = "billing_month", nullable = false)
    private Integer billingMonth; // Tháng (1-12)
    
    @Column(name = "billing_year", nullable = false)
    private Integer billingYear; // Năm
    
    @Column(name = "room_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal roomPrice; // Tiền phòng
    
    @Column(name = "electricity_cost", precision = 15, scale = 2)
    private BigDecimal electricityCost; // Tiền điện
    
    @Column(name = "water_cost", precision = 15, scale = 2)
    private BigDecimal waterCost; // Tiền nước
    
    @Column(name = "service_cost", precision = 15, scale = 2)
    private BigDecimal serviceCost; // Tiền dịch vụ khác
    
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount; // Tổng tiền
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BillStatus status; // Trạng thái
    
    @Column(name = "due_date")
    private LocalDate dueDate; // Hạn thanh toán
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate; // Ngày thanh toán thực tế
    
    @Column(name = "note", length = 500)
    private String note; // Ghi chú
    
    @Column(name = "payment_method", length = 50)
    private String paymentMethod; // Phương thức thanh toán (chuyển khoản, tiền mặt, etc.)
    
    @Column(name = "transaction_code", length = 100)
    private String transactionCode; // Mã giao dịch
}
