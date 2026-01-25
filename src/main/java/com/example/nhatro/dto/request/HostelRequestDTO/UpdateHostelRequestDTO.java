package com.example.nhatro.dto.request.HostelRequestDTO;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateHostelRequestDTO {
    private String name;
    private String address;
    private String description;
    private Double price;
    private Integer roomCount;
    private String district;
    private String city;
    private double area;
    private String roomType;
    private Integer maxOccupancy;
    private String amenities;
    private String elecUnitPrice;
    private String waterUnitPrice;
    private String wifiUnitPrice;
    private String parkingUnitPrice;
    private String trashUnitPrice;
    private List<MultipartFile> imageFiles;

    // For backward compatibility - accept image URLs directly
    private List<String> images;
}
