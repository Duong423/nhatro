package com.example.nhatro.dto.request.VehicleRequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVehicleRequestDTO {
    private String licensePlates; // Nhiều biển cách nhau bằng dấu phẩy
    private Long vehicleId;
    private Long contractId; 
    private String roomCode; 
} 
