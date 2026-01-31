package com.example.nhatro.service;

import java.util.List;

import com.example.nhatro.dto.request.VehicleRequestDTO.CreateVehicleRequestDTO;
import com.example.nhatro.dto.request.VehicleRequestDTO.UpdateVehicleRequestDTO;
import com.example.nhatro.dto.response.VehicleResponseDTO;

public interface VehicleService {
    VehicleResponseDTO createVehicle(CreateVehicleRequestDTO request);
    VehicleResponseDTO getVehicleByContract(Long contractId);
    VehicleResponseDTO getVehicleByRoomCode(String roomCode);
    List<VehicleResponseDTO> getAllVehicles();
    VehicleResponseDTO updateVehicle(Long vehicleId, UpdateVehicleRequestDTO request);
    void deleteVehicle(Long vehicleId);
}
