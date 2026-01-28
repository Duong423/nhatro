package com.example.nhatro.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.nhatro.enums.ContractStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contracts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking; // Hợp đồng liên kết với booking

    @ManyToOne
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant; // Người thuê (customer)

    @ManyToOne
    @JoinColumn(name = "landlord_id", nullable = false)
    private User landlord; // Chủ nhà (owner)

    @ManyToOne
    @JoinColumn(name = "hostel_id", nullable = false)
    private Hostel hostel; // Nhà trọ

    @Column(nullable = false)
    private LocalDate startDate; // Ngày bắt đầu hợp đồng

    @Column(nullable = false)
    private LocalDate endDate; // Ngày kết thúc hợp đồng

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal monthlyRent; // Giá thuê hàng tháng

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal depositAmount; // Tiền cọc

    @Column(precision = 10, scale = 2)
    private BigDecimal electricityCostPerUnit; // Giá điện/kWh (VNĐ)

    @Column(precision = 10, scale = 2)
    private BigDecimal waterCostPerUnit; // Giá nước/m³ (VNĐ)

    @Column(precision = 10, scale = 2)
    private BigDecimal serviceFee; // Phí dịch vụ khác (internet, rác, vệ sinh...)

    @Column(length = 20)
    private String paymentCycle; // Chu kỳ thanh toán (MONTHLY, QUARTERLY, YEARLY)

    @Column
    private Integer numberOfTenants; // Số lượng người ở

    @Column(columnDefinition = "TEXT")
    private String terms; // Điều khoản hợp đồng

    @Column
    private LocalDate signedDate; // Ngày ký hợp đồng

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus status = ContractStatus.PENDING; // Trạng thái hợp đồng

    @Column(columnDefinition = "TEXT")
    private String notes; // Ghi chú
}