package com.example.nhatro.dto.request.VehicleRequestDTO;

import lombok.Data;

@Data
public class CreateVehicleRequestDTO {
    private Long contractId;
    private String roomCode; 
    private String licensePlates; // Nhiều biển cách nhau bằng dấu phẩy
} 
