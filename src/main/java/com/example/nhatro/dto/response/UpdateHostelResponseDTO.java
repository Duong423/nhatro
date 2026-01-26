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
    private String contactPhone;
    private String contactEmail;
    private String contactName;
    private Double area;

    private String amenities;

    private List<String> imageUrls;

}
