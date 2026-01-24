package com.example.nhatro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavoriteResponseDTO {
    private Long id;
    private Long userId;
    private Long hostelId;
    // private String hostelName;
    // private String hostelAddress;

}
