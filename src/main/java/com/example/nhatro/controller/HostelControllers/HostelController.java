package com.example.nhatro.controller.HostelControllers;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.dto.response.HostelResponseDto;
import com.example.nhatro.service.HostelService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.nhatro.config.IsOwner;
import com.example.nhatro.dto.request.HostelRequestDTO.HostelRequestDto;

@RestController
@RequestMapping("/api/hostels")
public class HostelController {
    @Autowired
    private HostelService hostelService;

    /**
     * Tạo hostel mới với JSON (không upload ảnh)
     */
    @IsOwner
    @PostMapping
    public ApiResponse<HostelResponseDto> addHostel(@RequestBody @Valid HostelRequestDto hostelRequestDto) {
        HostelResponseDto hostel = hostelService.addHostel(hostelRequestDto);
        return ApiResponse.<HostelResponseDto>builder()
                .code(201)
                .message("Hostel added successfully")
                .result(hostel)
                .build();
    }

    /**
     * Upload ảnh cho hostel đã tồn tại
     */
    @IsOwner
    @PostMapping("/{hostelId}/images")
    public ApiResponse<HostelResponseDto> uploadHostelImages(
            @PathVariable Long hostelId,
            @RequestParam("imageFiles") List<MultipartFile> imageFiles) {
        try {
            HostelResponseDto hostel = hostelService.uploadHostelImages(hostelId, imageFiles);
            return ApiResponse.<HostelResponseDto>builder()
                    .code(200)
                    .message("Images uploaded successfully")
                    .result(hostel)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to upload images: " + e.getMessage());
        }
    }
}
