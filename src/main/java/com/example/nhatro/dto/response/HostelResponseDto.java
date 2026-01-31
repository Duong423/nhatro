package com.example.nhatro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelResponseDto {
    
    private Long hostelId;
    private Long ownerId;
    private String name;
    private String address;
    private String roomCode;
    private Double price;
    private Double area;
    private BigDecimal depositAmount; 
    private String status;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private List<String> imageUrls;
    private String description;
    private String amenities;
    private LocalDateTime createdAt;
    
   
}
