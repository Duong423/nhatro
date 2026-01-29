package com.example.nhatro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.config.IsAuthenticated;
import com.example.nhatro.config.IsOwner;
import com.example.nhatro.dto.request.ContractRequestDTO.ContractRequestDTO;
import com.example.nhatro.dto.request.ContractRequestDTO.UpdateContractRequestDTO;
import com.example.nhatro.dto.response.ContractResponseDTO;
import com.example.nhatro.service.ContractService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    /**
     * OWNER
     * Tạo hợp đồng từ booking đã được confirmed
     * API: POST /api/contracts/create
     */
    @IsOwner
    @PostMapping("/create")
    public ApiResponse<ContractResponseDTO> createContract(@Valid @RequestBody ContractRequestDTO request) {
        try {
            ContractResponseDTO contract = contractService.createContractFromBooking(request);
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(201)
                    .message("Contract created successfully")
                    .result(contract)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(400)
                    .message("Error creating contract: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * AUTHENTICATED - TENANT & LANDLORD
     * Lấy chi tiết hợp đồng
     * API: GET /api/contracts/{contractId}
     */
    @IsAuthenticated
    @GetMapping("/{contractId}")
    public ApiResponse<ContractResponseDTO> getContract(@PathVariable Long contractId) {
        try {
            ContractResponseDTO contract = contractService.getContractById(contractId);
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(200)
                    .message("Contract retrieved successfully")
                    .result(contract)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(400)
                    .message("Error retrieving contract: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * AUTHENTICATED
     * Lấy hợp đồng theo booking ID
     * API: GET /api/contracts/booking/{bookingId}
     */
    @IsAuthenticated
    @GetMapping("/booking/{bookingId}")
    public ApiResponse<ContractResponseDTO> getContractByBooking(@PathVariable Long bookingId) {
        try {
            ContractResponseDTO contract = contractService.getContractByBookingId(bookingId);
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(200)
                    .message("Contract retrieved successfully")
                    .result(contract)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(404)
                    .message("Error retrieving contract: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * AUTHENTICATED - TENANT
     * Lấy danh sách hợp đồng của người thuê (tenant)
     * API: GET /api/contracts/my-contracts
     */
    @IsAuthenticated
    @GetMapping("/my-contracts")
    public ApiResponse<List<ContractResponseDTO>> getMyContracts() {
        try {
            List<ContractResponseDTO> contracts = contractService.getContractsByTenant();
            return ApiResponse.<List<ContractResponseDTO>>builder()
                    .code(200)
                    .message("Contracts retrieved successfully")
                    .result(contracts)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<List<ContractResponseDTO>>builder()
                    .code(400)
                    .message("Error retrieving contracts: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * OWNER
     * Lấy danh sách hợp đồng của chủ nhà (landlord)
     * API: GET /api/contracts/owner/all
     */
    @IsOwner
    @GetMapping("/owner/all")
    public ApiResponse<List<ContractResponseDTO>> getOwnerContracts() {
        try {
            List<ContractResponseDTO> contracts = contractService.getContractsByOwner();
            return ApiResponse.<List<ContractResponseDTO>>builder()
                    .code(200)
                    .message("Contracts retrieved successfully")
                    .result(contracts)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<List<ContractResponseDTO>>builder()
                    .code(400)
                    .message("Error retrieving contracts: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * AUTHENTICATED - TENANT & LANDLORD
     * Ký hợp đồng (chuyển từ PENDING sang ACTIVE)
     * API: PUT /api/contracts/{contractId}/sign
     */
    @IsAuthenticated
    @PutMapping("/{contractId}/sign")
    public ApiResponse<ContractResponseDTO> signContract(@PathVariable Long contractId) {
        try {
            ContractResponseDTO contract = contractService.signContract(contractId);
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(200)
                    .message("Contract signed successfully")
                    .result(contract)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(400)
                    .message("Error signing contract: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * OWNER
     * Chấm dứt hợp đồng
     * API: PUT /api/contracts/{contractId}/terminate
     */
    @IsOwner
    @PutMapping("/{contractId}/terminate")
    public ApiResponse<ContractResponseDTO> terminateContract(
            @PathVariable Long contractId,
            @RequestParam(required = false) String reason) {
        try {
            String terminationReason = reason != null ? reason : "No reason provided";
            ContractResponseDTO contract = contractService.terminateContract(contractId, terminationReason);
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(200)
                    .message("Contract terminated successfully")
                    .result(contract)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(400)
                    .message("Error terminating contract: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }

    /**
     * OWNER
     * Cập nhật thông tin hợp đồng
     * API: PUT /api/contracts/{contractId}/update
     */
    @IsOwner
    @PutMapping("/{contractId}/update")
    public ApiResponse<ContractResponseDTO> updateContract(
            @PathVariable Long contractId,
            @Valid @RequestBody UpdateContractRequestDTO request) {
        try {
            ContractResponseDTO contract = contractService.updateContract(contractId, request);
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(200)
                    .message("Contract updated successfully")
                    .result(contract)
                    .build();
        } catch (RuntimeException e) {
            return ApiResponse.<ContractResponseDTO>builder()
                    .code(400)
                    .message("Error updating contract: " + e.getMessage())
                    .result(null)
                    .build();
        }
    }
}
