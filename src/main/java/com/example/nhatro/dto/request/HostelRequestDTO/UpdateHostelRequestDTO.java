package com.example.nhatro.dto.request.HostelRequestDTO;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHostelRequestDTO {
    private String name;
    private String address;
    private String roomCode;
    private String description;
    private Double price;
    private String contactPhone;
    private String contactEmail;
    private String contactName;
    private Double area;
    private Double depositAmount;
    private String amenities;


    /*Field này nhận file ảnh từ form-data đẩy lên cloudinary */
    private List<MultipartFile> imageFiles;

    // Nhận link ảnh trả về từ cloudinary và đẩy vào DB
    private List<String> images;
}
