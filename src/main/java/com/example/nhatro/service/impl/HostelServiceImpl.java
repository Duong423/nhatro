package com.example.nhatro.service.impl;

import com.example.nhatro.dto.request.HostelRequestDTO.HostelRequestDto;
import com.example.nhatro.dto.request.HostelRequestDTO.UpdateHostelRequestDTO;
import com.example.nhatro.dto.response.HostelResponseDto;
import com.example.nhatro.dto.response.UpdateHostelResponseDTO;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.entity.User;
// (Removed import for entity Service to avoid name clash)

import com.example.nhatro.mapper.HostelMapper;
import com.example.nhatro.repository.HostelRepository;
import com.example.nhatro.repository.UserRepository;
import com.example.nhatro.service.CloudinaryService;
import com.example.nhatro.service.HostelService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HostelServiceImpl implements HostelService {
    @Autowired
    private HostelRepository hostelRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CloudinaryService cloudinaryService;
    
  
    
    @PersistenceContext
    private EntityManager entityManager;

    

    /** 
     * Tạo hostel mới
     */
    @Override
    @Transactional
    public HostelResponseDto addHostelWithImages(HostelRequestDto dto) {
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
        // hostel.setDistrict(dto.getDistrict());
        // hostel.setCity(dto.getCity());
        hostel.setPrice(dto.getPrice());
        hostel.setArea(dto.getArea());
        hostel.setDescription(dto.getDescription());
        hostel.setAmenities(dto.getAmenities());

        
        // Upload images to Cloudinary
        List<String> imageUrls = new ArrayList<>();
        if (dto.getImageFiles() != null && !dto.getImageFiles().isEmpty()) {
            for (MultipartFile file : dto.getImageFiles()) {
                if (!file.isEmpty()) {
                    try {
                        String imageUrl = cloudinaryService.uploadFile(file);
                        imageUrls.add(imageUrl);
                    } catch (Exception e) {
                        System.err.println("Failed to upload image: " + file.getOriginalFilename() + " - " + e.getMessage());
                        // Continue ensuring other images might upload
                    }
                }
            }
        }
        
        // set image
        if (!imageUrls.isEmpty()) {
            hostel.setImages(String.join(",", imageUrls));
        }
        
        // Save hostel first to get ID
        Hostel savedHostel = hostelRepository.save(hostel);
        
        
        
        return HostelMapper.toResponseDto(savedHostel);
    }




    /**
     * Cập nhật ảnh cho hostel (xóa ảnh cũ và thêm ảnh mới)
     */
    @Override
    @Transactional
    public HostelResponseDto updateHostelImages(Long hostelId, List<MultipartFile> imageFiles, List<String> keepImages) {
        // Lấy hostel
        Hostel hostel = hostelRepository.findById(hostelId)
                .orElseThrow(() -> new RuntimeException("Hostel không tồn tại"));
        
        // Kiểm tra owner
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        if (!hostel.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật ảnh cho hostel này");
        }
        
        // Lấy danh sách ảnh hiện tại
        String existingImagesStr = hostel.getImages();
        List<String> currentImages = new ArrayList<>();
        if (existingImagesStr != null && !existingImagesStr.isEmpty()) {
            currentImages = List.of(existingImagesStr.split(","));
        }
        
        // Xác định ảnh cần xóa (không nằm trong keepImages)
        List<String> imagesToDelete = new ArrayList<>();
        if (keepImages == null) {
            keepImages = new ArrayList<>();
        }
        
        for (String currentImage : currentImages) {
            if (!keepImages.contains(currentImage.trim())) {
                imagesToDelete.add(currentImage.trim());
            }
        }
        
        // Xóa ảnh trên cloud
        for (String imageUrl : imagesToDelete) {
            try {
                cloudinaryService.deleteFile(imageUrl);
            } catch (Exception e) {
                System.err.println("Failed to delete image: " + imageUrl + " - " + e.getMessage());
            }
        }
        
        // Upload ảnh mới
        List<String> newImageUrls = new ArrayList<>();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadFile(file);
                    newImageUrls.add(imageUrl);
                }
            }
        }
        
        // Gộp danh sách ảnh giữ lại + ảnh mới
        List<String> finalImages = new ArrayList<>(keepImages);
        finalImages.addAll(newImageUrls);
        
        // Lưu danh sách ảnh mới
        if (finalImages.isEmpty()) {
            hostel.setImages(null);
        } else {
            hostel.setImages(String.join(",", finalImages));
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
     * Lấy danh sách hostel của owner hiện tại
     */
    @Override
    public List<HostelResponseDto> getHostelsByOwner() {
        // Lấy thông tin owner đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        // Lấy danh sách hostel của owner này
        List<Hostel> hostels = hostelRepository.findByOwnerId(owner.getId());
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

    /*
     * Cập nhật hostel
    */
    @Override
    @Transactional
    public UpdateHostelResponseDTO updateHostel(Long hostelId, UpdateHostelRequestDTO hostelRequestDTO) {
        // Lấy hostel
        Hostel hostel = hostelRepository.findById(hostelId)
                .orElseThrow(() -> new RuntimeException("Hostel không tồn tại"));
        
        // Kiểm tra owner
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        
        if (!hostel.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật hostel này");
        }
        
        // Cập nhật thông tin hostel - chỉ update các field không null
        if (hostelRequestDTO.getName() != null) {
            hostel.setName(hostelRequestDTO.getName());
        }
        if (hostelRequestDTO.getAddress() != null) {
            hostel.setAddress(hostelRequestDTO.getAddress());
        }
        if (hostelRequestDTO.getContactPhone() != null) {
            hostel.setContactPhone(hostelRequestDTO.getContactPhone());
        }
        if (hostelRequestDTO.getContactEmail() != null) {
            hostel.setContactEmail(hostelRequestDTO.getContactEmail());
        }
        if (hostelRequestDTO.getContactName() != null) {
            hostel.setContactName(hostelRequestDTO.getContactName());
        }
        if (hostelRequestDTO.getPrice() != null) {
            hostel.setPrice(hostelRequestDTO.getPrice());
        }
        if (hostelRequestDTO.getArea() != null) {
            hostel.setArea(hostelRequestDTO.getArea());
        }
        if (hostelRequestDTO.getDescription() != null) {
            hostel.setDescription(hostelRequestDTO.getDescription());
        }
        if (hostelRequestDTO.getAmenities() != null) {
            hostel.setAmenities(hostelRequestDTO.getAmenities());
        }
        if (hostelRequestDTO.getImages() != null && !hostelRequestDTO.getImages().isEmpty()) {
            hostel.setImages(String.join(",", hostelRequestDTO.getImages()));
        }
        
        Hostel updatedHostel = hostelRepository.save(hostel);
        
        // Convert images string to list
        List<String> imageList = updatedHostel.getImages() != null && !updatedHostel.getImages().isEmpty() 
            ? Arrays.asList(updatedHostel.getImages().split(","))
            : new ArrayList<>();
        
        return new UpdateHostelResponseDTO(
            updatedHostel.getHostelId(),
            updatedHostel.getName(),
            updatedHostel.getAddress(),
            updatedHostel.getDescription(),
            updatedHostel.getPrice(),
            updatedHostel.getContactPhone(),
            updatedHostel.getContactEmail(),
            updatedHostel.getContactName(),
            updatedHostel.getArea(),
            updatedHostel.getAmenities(),
            imageList
        );
        // return HostelMapper.toResponseDto(updatedHostel);
    }


    /**
     * Xóa hostel
     */
    @Override
    @Transactional
    public void deleteHostel(Long hostelId) {
        Hostel hostel = hostelRepository.findById(hostelId)
                .orElseThrow(() -> new RuntimeException("Hostel không tồn tại"));

        // Kiểm tra owner
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (!hostel.getOwner().getId().equals(owner.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa hostel này");
        }

        hostelRepository.delete(hostel);
    }
}