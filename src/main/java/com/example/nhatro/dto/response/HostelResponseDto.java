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
    private String ownerName;
    private String name;
    private String address;
    private String district;
    private String city;
    private Double price;
    private Double area;
    private String roomType;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Integer roomCount;
    private Integer maxOccupancy;
    private List<String> imageUrls;
    private String description;
    private BigDecimal elecUnitPrice;
    private BigDecimal waterUnitPrice;
    private LocalDateTime createdAt;
    
    // Nested services
    private List<ServiceInHostelResponseDto> services;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceInHostelResponseDto {
        private Long serviceId;
        private String serviceName;
        private BigDecimal price;
        private String unit;
    }
}
