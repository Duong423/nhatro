package com.example.nhatro.service.impl;

import com.example.nhatro.dto.request.HostelRequestDTO.HostelRequestDto;
import com.example.nhatro.dto.request.ServiceRequestDTO.ServiceInHostelDto;
import com.example.nhatro.dto.response.HostelResponseDto;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.entity.User;
// (Removed import for entity Service to avoid name clash)

import com.example.nhatro.mapper.HostelMapper;
import com.example.nhatro.repository.HostelRepository;
import com.example.nhatro.repository.UserRepository;
import com.example.nhatro.service.CloudinaryService;
import com.example.nhatro.service.HostelService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class HostelServiceImpl implements HostelService {
    @Autowired
    private HostelRepository hostelRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
    @Autowired
    private com.example.nhatro.service.ServiceService serviceService;
    
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public HostelResponseDto addHostel(HostelRequestDto dto) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Hostel hostel = new Hostel();
        hostel.setOwner(owner);
        
        // Auto-fill contact info from owner
        hostel.setContactName(owner.getFullName());
        hostel.setContactEmail(owner.getEmail());
        hostel.setContactPhone(owner.getPhone());

        hostel.setName(dto.getTitle());
        hostel.setAddress(dto.getAddress());
        hostel.setDistrict(dto.getDistrict());
        hostel.setCity(dto.getCity());
        hostel.setPrice(dto.getPrice());
        hostel.setArea(dto.getArea());
        hostel.setDescription(dto.getDescription());
        hostel.setRoomCount(dto.getRoomCount());
        hostel.setMaxOccupancy(dto.getMaxOccupancy());
        hostel.setRoomType(dto.getRoomType());
        
        // Lưu hostel trước
        Hostel savedHostel = hostelRepository.save(hostel);
        
        // Tạo services cho hostel 
        if (dto.getServices() != null && !dto.getServices().isEmpty()) {
            List<com.example.nhatro.entity.ServiceHostel> createdServices = new ArrayList<>();
            for (ServiceInHostelDto serviceDto : dto.getServices()) {
                try {
                    com.example.nhatro.entity.ServiceHostel service = serviceService.createServiceForNewHostel(savedHostel, serviceDto);
                    createdServices.add(service);
                } catch (Exception e) {
                    // Log error nhưng không throw để không ảnh hưởng việc tạo hostel
                    System.err.println("Error creating service: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            // Cập nhật list services vào object hostel để trả về response đúng
            savedHostel.setServices(createdServices);
        }
        
        return HostelMapper.toResponseDto(savedHostel);
    }

    @Override
    public HostelResponseDto uploadHostelImages(Long hostelId, List<MultipartFile> imageFiles) {
        // Lấy hostel
        Hostel hostel = hostelRepository.findById(hostelId)
                .orElseThrow(() -> new RuntimeException("Hostel không tồn tại"));
        
        // Kiểm tra owner
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        if (!hostel.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền upload ảnh cho hostel này");
        }
        
        // Upload ảnh
        List<String> imageUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadFile(file);
                    imageUrls.add(imageUrl);
                }
            }
        }
        
        // Cập nhật ảnh cho hostel
        if (!imageUrls.isEmpty()) {
            String existingImages = hostel.getImages();
            if (existingImages != null && !existingImages.isEmpty()) {
                imageUrls.add(0, existingImages);
            }
            hostel.setImages(String.join(",", imageUrls));
        }
        
        Hostel savedHostel = hostelRepository.save(hostel);
        return HostelMapper.toResponseDto(savedHostel);
    }
     

    /**
     * Lấy chi tiết toàn bộ phòng trọ
     */
    @Override
    public List<HostelResponseDto> getAllHostelsForTenant() {
        List<Hostel> hostels = hostelRepository.findAll();
        List<HostelResponseDto> responseDtos = new ArrayList<>();
        for (Hostel hostel : hostels) {
            responseDtos.add(HostelMapper.toResponseDto(hostel));
        }
        return responseDtos;
    }
    

    /**
     * Lấy chi tiết 1 hostel theo ID
     */

    @Override
    public HostelResponseDto getHostelById(Long hostelId) {
        Hostel hostel = hostelRepository.findByIdWithServices(hostelId)
                .orElseThrow(() -> new RuntimeException("Hostel không tồn tại"));
        return HostelMapper.toResponseDto(hostel);
    }
}
