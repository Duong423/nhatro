package com.example.nhatro.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

import com.example.nhatro.enums.ServiceUnit;

@Entity
@Table(name = "services")
@Data
public class ServiceHostel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;

    @ManyToOne
    @JoinColumn(name = "hostel_id")
    private Hostel hostel;

    private String serviceName; // Wifi, Rác, Gửi xe...
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private ServiceUnit unit; // ROOM, PERSON, INDEX...
}