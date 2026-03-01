package com.example.nhatro.service;

import java.util.List;

import com.example.nhatro.dto.request.ContractRequestDTO.ContractRequestDTO;
import com.example.nhatro.dto.request.ContractRequestDTO.UpdateContractRequestDTO;
import com.example.nhatro.dto.response.ContractResponseDTO;
import com.example.nhatro.dto.response.TenantInfoDTO;

public interface ContractService {
    
    /**
     * Tạo hợp đồng từ booking đã được xác nhận
     */
    ContractResponseDTO createContractFromBooking(ContractRequestDTO request);
    
    /**
     * Lấy chi tiết hợp đồng
     */
    ContractResponseDTO getContractById(Long contractId);
    
    /**
     * Lấy hợp đồng theo booking ID
     */
    ContractResponseDTO getContractByBookingId(Long bookingId);
    
    /**
     * Lấy danh sách hợp đồng của tenant (khách thuê)
     */
    List<ContractResponseDTO> getContractsByTenant();
    
    /**
     * Lấy danh sách hợp đồng của landlord (chủ nhà)
     */
    List<ContractResponseDTO> getContractsByOwner();
    
    /**
     * Ký hợp đồng (chuyển từ PENDING sang ACTIVE)
     */
    ContractResponseDTO signContract(Long contractId);
    
    /**
     * Chấm dứt hợp đồng
     */
    ContractResponseDTO terminateContract(Long contractId, String reason);
    
    /**
     * Cập nhật thông tin hợp đồng
     */
    ContractResponseDTO updateContract(Long contractId, UpdateContractRequestDTO request);
    
    /**
     * Tìm kiếm hợp đồng theo số điện thoại (tenant hoặc owner)
     */
    List<ContractResponseDTO> searchContractsByPhone(String phone);

    /**
     * Lấy danh sách người thuê từ hợp đồng đang có hiệu lực (ACTIVE) của chủ nhà hiện tại
     */
    List<TenantInfoDTO> getTenantsFromActiveContracts();
}
