package com.example.nhatro.dto.request.HostelRequestDTO;

import java.security.Provider.Service;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.nhatro.dto.request.ServiceRequestDTO.ServiceInHostelDto;

@Data

public class HostelRequestDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "City is required")
    private String city;

    @Positive(message = "Price must be positive")
    private double price;

    @Positive(message = "Area must be positive")
    private double area;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 1, message = "Room count must be at least 1")
    private int roomCount;

    @Min(value = 1, message = "Max occupancy must be at least 1")
    private int maxOccupancy;

    // For file upload from client
    private List<MultipartFile> imageFiles;

    // For backward compatibility - accept image URLs directly
    private List<String> images;

    @NotBlank(message = "Room type is required")
    private String roomType;

    // Danh sách services khi tạo hostel (có thể truyền array hoặc string)
    private List<ServiceInHostelDto> services;
    
}
