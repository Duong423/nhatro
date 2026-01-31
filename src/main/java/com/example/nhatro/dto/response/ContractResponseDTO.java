package com.example.nhatro.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDTO {
    
    private Long contractId;
    private Long bookingId;

    private Long tenantId;
    private String tenantName;
    private String phoneNumberTenant;
    private String tenantEmail;
    private String cccd;

    private Long ownerId;
    private String ownerName;
    private String phoneNumberOwner;

    private Long hostelId;
    private String hostelName;
    private String hostelAddress;
    private String hostelRoomCode;
    private Double hostelPrice;
    private Double hostelArea;
    private String hostelAmenities;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal monthlyRent;
    private BigDecimal depositAmount;
    private BigDecimal electricityCostPerUnit;
    private BigDecimal waterCostPerUnit;
    private BigDecimal serviceFee;

    private String paymentCycle;
    private Integer numberOfTenants;

    private String terms;
    private LocalDate signedDate;
    private String status;
    
    private String notes;

    private Long vehicleId;
    private String vehicleLicensePlates;

    private LocalDateTime createdAt;
}
