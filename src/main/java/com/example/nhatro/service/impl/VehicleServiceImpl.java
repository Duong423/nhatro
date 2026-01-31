package com.example.nhatro.service.impl;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nhatro.dto.request.VehicleRequestDTO.CreateVehicleRequestDTO;
import com.example.nhatro.dto.request.VehicleRequestDTO.UpdateVehicleRequestDTO;
import com.example.nhatro.dto.response.VehicleResponseDTO;
import com.example.nhatro.entity.Contract;
import com.example.nhatro.entity.Vehicle;
import com.example.nhatro.enums.ContractStatus;
import com.example.nhatro.exception.ResourceNotFoundException;
import com.example.nhatro.repository.ContractRepository;
import com.example.nhatro.repository.VehicleRepository;
import com.example.nhatro.service.VehicleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public VehicleResponseDTO createVehicle(CreateVehicleRequestDTO request) {
        Contract contract = null;
        if (request.getContractId() != null) {
            contract = contractRepository.findById(request.getContractId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contract not found with ID: " + request.getContractId()));
        } else if (request.getRoomCode() != null) {
            contract = contractRepository.findByHostel_RoomCodeAndStatus(request.getRoomCode(), ContractStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active contract not found for room code: " + request.getRoomCode()));
        } else {
            throw new IllegalArgumentException("Either contractId or roomCode must be provided");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlates(request.getLicensePlates());

        vehicle = vehicleRepository.save(vehicle);

        // Set the vehicle_id on contract (contract owns the FK)
        contract.setVehicle(vehicle);
        contractRepository.save(contract);

        // maintain in-memory bidirectional link
        vehicle.setContract(contract);

        return mapToDto(vehicle);
    }

    @Override
    public VehicleResponseDTO getVehicleByContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + contractId));
        Vehicle v = contract.getVehicle();
        if (v == null) {
            throw new ResourceNotFoundException("Vehicle not found for contract ID: " + contractId);
        }
        return mapToDto(v);
    }

    @Override
    public VehicleResponseDTO getVehicleByRoomCode(String roomCode) {
        Contract contract = contractRepository.findByHostel_RoomCodeAndStatus(roomCode, ContractStatus.ACTIVE)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Active contract not found for room code: " + roomCode));
        Vehicle v = contract.getVehicle();
        if (v == null) {
            throw new ResourceNotFoundException("Vehicle not found for room code: " + roomCode);
        }
        return mapToDto(v);
    }

    @Override
    public List<VehicleResponseDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleResponseDTO updateVehicle(Long vehicleId, UpdateVehicleRequestDTO request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        if (request.getLicensePlates() != null) {
            vehicle.setLicensePlates(request.getLicensePlates());
        }

        if (request.getContractId() != null || request.getRoomCode() != null) {
            Contract targetContract;
            if (request.getContractId() != null) {
                targetContract = contractRepository.findById(request.getContractId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Contract not found with ID: " + request.getContractId()));
            } else {
                targetContract = contractRepository
                        .findByHostel_RoomCodeAndStatus(request.getRoomCode(), ContractStatus.ACTIVE)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Active contract not found for room code: " + request.getRoomCode()));
            }

            // If target contract already has a different vehicle, reject
            if (targetContract.getVehicle() != null
                    && !targetContract.getVehicle().getVehicleId().equals(vehicle.getVehicleId())) {
                throw new IllegalArgumentException("Target contract already has a different vehicle assigned");
            }

            // Clear old contract link if present
            Contract oldContract = vehicle.getContract();
            if (oldContract != null && !oldContract.getContractId().equals(targetContract.getContractId())) {
                oldContract.setVehicle(null);
                contractRepository.save(oldContract);
            }

            // Assign to new contract
            targetContract.setVehicle(vehicle);
            contractRepository.save(targetContract);
            vehicle.setContract(targetContract);
        }

        vehicle = vehicleRepository.save(vehicle);
        return mapToDto(vehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        Contract contract = vehicle.getContract();
        if (contract != null) {
            contract.setVehicle(null);
            contractRepository.save(contract);
        }

        vehicleRepository.delete(vehicle);
    }

    private VehicleResponseDTO mapToDto(Vehicle v) {
        VehicleResponseDTO dto = new VehicleResponseDTO();
        dto.setVehicleId(v.getVehicleId());
        dto.setContractId(v.getContract() != null ? v.getContract().getContractId() : null);
        dto.setRoomCode(v.getContract() != null && v.getContract().getHostel() != null
                ? v.getContract().getHostel().getRoomCode()
                : null);
        dto.setPhoneNumberTenant(
                v.getContract() != null && v.getContract().getTenant() != null ? v.getContract().getTenant().getPhone()
                        : null);
        dto.setNameTenant(
                v.getContract() != null && v.getContract().getTenant() != null ? v.getContract().getTenant().getName()
                        : null);

        dto.setLicensePlates(v.getLicensePlates());
        dto.setCreatedAt(v.getCreatedAt());
        dto.setUpdatedAt(v.getUpdatedAt());
        return dto;
    }
}
