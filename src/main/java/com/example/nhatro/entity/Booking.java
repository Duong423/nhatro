package com.example.nhatro.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.nhatro.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

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
@Data
@Table(name = "bookings")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Booking extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private User customer; // Người đặt phòng

    @OneToOne
    @JoinColumn(name = "hostel_id", nullable = false, unique = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "hostelId")
    @JsonIdentityReference(alwaysAsId = true)
    private Hostel hostel; // Nhà trọ được đặt (1 hostel chỉ có 1 booking)

    @Column(nullable = false)
    private LocalDateTime bookingDate; // Ngày đặt phòng

    @Column(nullable = false)
    private LocalDateTime checkInDate; // Ngày dự kiến vào ở

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal depositAmount; // Số tiền cọc

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING; // Trạng thái booking

    @Column(columnDefinition = "TEXT")
    private String notes; // Ghi chú từ khách hàng

    @Column(length = 50)
    private String customerName; // Tên người đặt

    @Column(length = 20)
    private String customerPhone; // SĐT người đặt

    @Column(length = 100)
    private String customerEmail; // Email người đặt

    @OneToOne(mappedBy = "booking")
    private Payment payment; // Payment liên kết
}
