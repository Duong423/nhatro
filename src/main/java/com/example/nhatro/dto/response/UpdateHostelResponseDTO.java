package com.example.nhatro.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateHostelResponseDTO {
    private Long hostelId;
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
    private List<String> imageUrls;


}
