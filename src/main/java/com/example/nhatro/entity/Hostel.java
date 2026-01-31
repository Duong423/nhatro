package com.example.nhatro.entity;

import java.math.BigDecimal;

import com.example.nhatro.enums.HostelStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "hostels")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Hostel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hostelId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private User owner;
    private String name;
    private String address;

    private String district;
    private String city;
    private Double price;
    private Double area;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal depositAmount; // Tiền cọc
   
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    @Column(name = "room_code")
    private String roomCode;

    // Store amenities and images as JSON or comma-separated string for simplicity
    @Column(columnDefinition = "TEXT")
    private String amenities; // e.g., "wifi,aircon,parking"

    @Column(columnDefinition = "TEXT")
    private String images; // e.g., URLs separated by comma

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HostelStatus status = HostelStatus.AVAILABLE; // Mặc định là còn phòng


}
