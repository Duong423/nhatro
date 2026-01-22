package com.example.nhatro.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
    private String roomType;
   
    private String contactName;
    private String contactPhone;
    private String contactEmail;
   
    private Integer roomCount;
    private Integer maxOccupancy;

    // Store amenities and images as JSON or comma-separated string for simplicity
    @Column(columnDefinition = "TEXT")
    private String amenities; // e.g., "wifi,aircon,parking"

    @Column(columnDefinition = "TEXT")
    private String images; // e.g., URLs separated by comma

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal elecUnitPrice;
    private BigDecimal waterUnitPrice;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Room> rooms;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ServiceHostel> services;

}
