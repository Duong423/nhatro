package com.example.nhatro.dto.response;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class VehicleResponseDTO {
    private Long vehicleId;
    private Long contractId;
    private String phoneNumberTenant;
    private String nameTenant;
    private String roomCode;
    private String licensePlates;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
