package com.example.nhatro.service;

import com.example.nhatro.dto.request.ServiceRequestDTO.ServiceInHostelDto;
import com.example.nhatro.dto.request.ServiceRequestDTO.ServiceRequestDto;
import com.example.nhatro.dto.response.ServiceResponseDto;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.entity.Service;

import java.util.List;

public interface ServiceService {
    
    /**
     * Thêm dịch vụ mới (dành cho admin)
     */
    ServiceResponseDto addService(ServiceRequestDto request);
    
    /**
     * Owner thêm dịch vụ vào hostel của mình
     */
    ServiceResponseDto addServiceByOwner(ServiceRequestDto request, String ownerEmail);
    
    /**
     * Tạo service cho hostel mới (không cần check hostel tồn tại)
     * Dùng khi tạo hostel và services cùng lúc
     */
    Service createServiceForNewHostel(Hostel hostel, ServiceInHostelDto serviceDto);
    
    /**
     * Lấy tất cả dịch vụ (admin)
     */
    List<ServiceResponseDto> getAllServices();
    
    /**
     * Lấy dịch vụ của owner hiện tại
     */
    List<ServiceResponseDto> getServicesForCurrentOwner(String ownerEmail);
    
    /**
     * Admin cập nhật dịch vụ bất kỳ
     */
    ServiceResponseDto updateService(Long serviceId, ServiceRequestDto request);
    
    /**
     * Owner cập nhật dịch vụ của mình (tự động check ownership)
     */
    ServiceResponseDto updateServiceByOwner(Long serviceId, ServiceRequestDto request, String ownerEmail);
    
    /**
     * Admin xóa dịch vụ
     */
    void deleteService(Long serviceId);
    
    /**
     * Owner xóa dịch vụ của mình (tự động check ownership)
     */
    void deleteServiceByOwner(Long serviceId, String ownerEmail);
}
