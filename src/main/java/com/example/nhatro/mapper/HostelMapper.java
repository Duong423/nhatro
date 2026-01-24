package com.example.nhatro.mapper;

import com.example.nhatro.dto.response.HostelResponseDto;
import com.example.nhatro.entity.Hostel;
import com.example.nhatro.entity.ServiceHostel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HostelMapper {
    
    public static HostelResponseDto toResponseDto(Hostel hostel) {
        if (hostel == null) {
            return null;
        }

        // Convert images string to List<String>
        List<String> imageUrls = Collections.emptyList();
        if (hostel.getImages() != null && !hostel.getImages().isEmpty()) {
            imageUrls = Arrays.stream(hostel.getImages().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        }

        // Convert services to nested DTO
        List<HostelResponseDto.ServiceInHostelResponseDto> serviceDtos = Collections.emptyList();
        if (hostel.getServices() != null && !hostel.getServices().isEmpty()) {
            serviceDtos = hostel.getServices().stream()
                .map(service -> HostelResponseDto.ServiceInHostelResponseDto.builder()
                    .serviceId(service.getServiceId())
                    .serviceName(service.getServiceName())
                    .price(service.getPrice())
                    .unit(service.getUnit() != null ? service.getUnit().name() : null)
                    .build())
                .collect(Collectors.toList());
        }

        return HostelResponseDto.builder()
            .hostelId(hostel.getHostelId())
            .ownerId(hostel.getOwner() != null ? hostel.getOwner().getId() : null)
            .ownerName(hostel.getOwner() != null ? hostel.getOwner().getFullName() : null)
            .name(hostel.getName())
            .address(hostel.getAddress())
            .district(hostel.getDistrict())
            .city(hostel.getCity())
            .price(hostel.getPrice())
            .area(hostel.getArea())
            .roomType(hostel.getRoomType())
            .contactName(hostel.getContactName())
            .contactPhone(hostel.getContactPhone())
            .contactEmail(hostel.getContactEmail())
            .imageUrls(imageUrls)
            .roomCount(hostel.getRoomCount())
            .maxOccupancy(hostel.getMaxOccupancy())
            .description(hostel.getDescription())
            .amenities(hostel.getAmenities())
            .elecUnitPrice(hostel.getElecUnitPrice())
            .waterUnitPrice(hostel.getWaterUnitPrice())
            .createdAt(hostel.getCreatedAt())
            .services(serviceDtos)
            .build();
    }
}
