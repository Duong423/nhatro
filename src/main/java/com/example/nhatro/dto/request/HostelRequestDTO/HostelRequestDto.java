package com.example.nhatro.dto.request.HostelRequestDTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

@Data

public class HostelRequestDto {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Address is required")
    private String address;
    private String roomCode;

    // private String district;

    // @NotBlank(message = "City is required")
    // private String city;

    @Positive(message = "Price must be positive")
    private Double price;

    private Double area;
    
    private Double depositAmount; // Tiền cọc

    @NotBlank(message = "Description is required")
    private String description;

    private String amenities;

    /*Field này nhận file ảnh từ form-data đẩy lên cloudinary */
    private List<MultipartFile> imageFiles;

    // Nhận link ảnh trả về từ cloudinary và đẩy vào DB
    private List<String> images;

}
