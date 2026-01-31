package com.example.nhatro.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.config.IsOwner;
import com.example.nhatro.dto.request.VehicleRequestDTO.CreateVehicleRequestDTO;
import com.example.nhatro.dto.request.VehicleRequestDTO.UpdateVehicleRequestDTO;
import com.example.nhatro.dto.response.VehicleResponseDTO;
import com.example.nhatro.service.VehicleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    @IsOwner
    @PostMapping
    public ApiResponse<VehicleResponseDTO> create(@RequestBody CreateVehicleRequestDTO request) {
        VehicleResponseDTO dto = vehicleService.createVehicle(request);
        return ApiResponse.<VehicleResponseDTO>builder()
                .code(201)
                .message("Vehicle created successfully")
                .result(dto)
                .build();
    }

    @IsOwner
    @GetMapping
    public ApiResponse<List<VehicleResponseDTO>> getAll() {
        List<VehicleResponseDTO> dtos = vehicleService.getAllVehicles();
        return ApiResponse.<List<VehicleResponseDTO>>builder()
                .code(200)
                .message("Vehicles retrieved successfully")
                .result(dtos)
                .build();
    }

    @IsOwner
    @GetMapping("/room/{roomCode}")
    public ApiResponse<VehicleResponseDTO> getByRoom(@PathVariable String roomCode) {
        VehicleResponseDTO dto = vehicleService.getVehicleByRoomCode(roomCode);
        return ApiResponse.<VehicleResponseDTO>builder()
                .code(200)
                .message("Vehicle retrieved successfully")
                .result(dto)
                .build();
    }

    @IsOwner
    @PutMapping("/{vehicleId}")
    public ApiResponse<VehicleResponseDTO> update(@PathVariable Long vehicleId, @RequestBody UpdateVehicleRequestDTO request) {
        VehicleResponseDTO dto = vehicleService.updateVehicle(vehicleId, request);
        return ApiResponse.<VehicleResponseDTO>builder()
                .code(200)
                .message("Vehicle updated successfully")
                .result(dto)
                .build();
    }
    @IsOwner
    @DeleteMapping("/{vehicleId}")
    public ApiResponse<Void> delete(@PathVariable Long vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Vehicle deleted successfully")
                .result(null)
                .build();
    }
}
