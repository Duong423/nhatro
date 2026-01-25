package com.example.nhatro.dto.request.HostelRequestDTO;


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

    private String district;

    @NotBlank(message = "City is required")
    private String city;

    @Positive(message = "Price must be positive")
    private Double price;

    private Double area;

    @NotBlank(message = "Description is required")
    private String description;

    private String amenities; 

    private Integer roomCount;

    private Integer maxOccupancy;

    // For file upload from client
    private List<MultipartFile> imageFiles;

    // For backward compatibility - accept image URLs directly
    private List<String> images;

    private String roomType;
    
    private String elecUnitPrice;
    private String waterUnitPrice;
    private String wifiUnitPrice;
    private String parkingUnitPrice;
    private String trashUnitPrice;

    
}
