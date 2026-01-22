package com.example.nhatro.service.impl;

import com.example.nhatro.dto.request.ServiceRequestDTO.ServiceRequestDto;
import com.example.nhatro.dto.response.ServiceResponseDto;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.entity.ServiceHostel;
import com.example.nhatro.entity.User;
import com.example.nhatro.repository.HostelRepository;
import com.example.nhatro.repository.ServiceRepository;
import com.example.nhatro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements com.example.nhatro.service.ServiceService {
    
    private final ServiceRepository serviceRepository;
    private final HostelRepository hostelRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ServiceResponseDto addService(ServiceRequestDto request) {
        // Kiểm tra hostel có tồn tại không
        Hostel hostel = hostelRepository.findById(request.getHostelId())
                .orElseThrow(() -> new RuntimeException("Hostel không tồn tại"));
        
        // Tạo service mới
        ServiceHostel service = new ServiceHostel();
        service.setHostel(hostel);
        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setUnit(request.getUnit());
        
        ServiceHostel savedService = serviceRepository.save(service);
        
        return convertToDto(savedService);
    }

    @Override
    @Transactional
    public ServiceResponseDto addServiceByOwner(ServiceRequestDto request, String ownerEmail) {
        // Lấy thông tin owner
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // Kiểm tra hostel có tồn tại không
        Hostel hostel = hostelRepository.findById(request.getHostelId())
                .orElseThrow(() -> new RuntimeException("Hostel không tồn tại"));
        
        // Kiểm tra owner có sở hữu hostel này không
        if (!hostel.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền thêm dịch vụ vào hostel này");
        }
        
        // Tạo service mới
        ServiceHostel service = new ServiceHostel();
        service.setHostel(hostel);
        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setUnit(request.getUnit());
        
        ServiceHostel savedService = serviceRepository.save(service);
        
        return convertToDto(savedService);
    }

    @Override
    @Transactional
    public ServiceHostel createServiceForNewHostel(Hostel hostel, com.example.nhatro.dto.request.ServiceRequestDTO.ServiceInHostelDto serviceDto) {
        ServiceHostel service;
        
        if (serviceDto.isExistingService()) {
            // Chọn service có sẵn - copy thông tin từ service template
            ServiceHostel existingService = serviceRepository.findById(serviceDto.getServiceId())
                    .orElseThrow(() -> new RuntimeException("Service không tồn tại"));
            
            service = new ServiceHostel();
            service.setServiceName(existingService.getServiceName());
            service.setPrice(existingService.getPrice());
            service.setUnit(existingService.getUnit());
        } else {
            // Tạo service mới
            if (serviceDto.getServiceName() == null || serviceDto.getPrice() == null || serviceDto.getUnit() == null) {
                throw new RuntimeException("Thông tin service không đầy đủ");
            }
            
            service = new ServiceHostel();
            service.setServiceName(serviceDto.getServiceName());
            service.setPrice(serviceDto.getPrice());
            service.setUnit(serviceDto.getUnit());
        }
        
        // Gán hostel cho service (không cần check hostel tồn tại vì đang tạo cùng lúc)
        service.setHostel(hostel);
        
        return serviceRepository.save(service);
    }
    

    /// Get all services 
    @Override
    public List<ServiceResponseDto> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    

    ///Get all services of current owner
    @Override
    public List<ServiceResponseDto> getServicesForCurrentOwner(String ownerEmail) {
        // Lấy thông tin owner
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // Lấy tất cả hostels của owner
        List<Hostel> hostels = hostelRepository.findAll()
                .stream()
                .filter(h -> h.getOwner().getId().equals(owner.getId()))
                .collect(Collectors.toList());
        
        // Lấy tất cả services của các hostels này
        return hostels.stream()
                .flatMap(hostel -> serviceRepository.findByHostel_HostelId(hostel.getHostelId()).stream())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    
    ///Update service (admin)
    @Override
    @Transactional
    public ServiceResponseDto updateService(Long serviceId, ServiceRequestDto request) {
        // Lấy service
        ServiceHostel service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service không tồn tại"));
        
        // Kiểm tra hostel có tồn tại không (nếu request có hostelId)
        if (request.getHostelId() != null) {
            Hostel hostel = hostelRepository.findById(request.getHostelId())
                    .orElseThrow(() -> new RuntimeException("Hostel không tồn tại"));
            service.setHostel(hostel);
        }
        
        // Update thông tin service
        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setUnit(request.getUnit());
        
        ServiceHostel updatedService = serviceRepository.save(service);
        
        return convertToDto(updatedService);
    }

    ///Update service by owner
    @Override
    @Transactional
    public ServiceResponseDto updateServiceByOwner(Long serviceId, ServiceRequestDto request, String ownerEmail) {
        // Lấy thông tin owner
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // Lấy service
        ServiceHostel service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service không tồn tại"));
        
        // Kiểm tra owner có sở hữu service này không (qua hostel)
        if (!service.getHostel().getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật dịch vụ này");
        }
        
        // Không cho phép đổi hostel, chỉ update thông tin service
        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setUnit(request.getUnit());
        
        ServiceHostel updatedService = serviceRepository.save(service);
        
        return convertToDto(updatedService);
    }
    
    //Delete service by admin
    @Override
    @Transactional
    public void deleteService(Long serviceId) {
        // Admin có thể xóa bất kỳ service nào
        if (!serviceRepository.existsById(serviceId)) {
            throw new RuntimeException("Service không tồn tại");
        }
        serviceRepository.deleteById(serviceId);
    }

    @Override
    @Transactional
    public void deleteServiceByOwner(Long serviceId, String ownerEmail) {
        // Lấy thông tin owner
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // Lấy service
        ServiceHostel service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service không tồn tại"));
        
        // Kiểm tra owner có sở hữu service này không (qua hostel)
        if (!service.getHostel().getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa dịch vụ này");
        }
        
        serviceRepository.deleteById(serviceId);
    }
    
    /**
     * Convert entity to DTO
     */
    private ServiceResponseDto convertToDto(ServiceHostel service) {
        return ServiceResponseDto.builder()
                .serviceId(service.getServiceId())
                .hostelId(service.getHostel().getHostelId())
                .hostelName(service.getHostel().getName())
                .serviceName(service.getServiceName())
                .price(service.getPrice())
                .unit(service.getUnit())
                .build();
    }
}
