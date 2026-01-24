package com.example.nhatro.controller.HostelControllers;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.dto.response.HostelResponseDto;
import com.example.nhatro.dto.response.UpdateHostelResponseDTO;
import com.example.nhatro.service.HostelService;
import com.mysql.cj.x.protobuf.MysqlxCrud.Update;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.nhatro.config.IsOwner;
import com.example.nhatro.dto.request.HostelRequestDTO.HostelRequestDto;
import com.example.nhatro.dto.request.HostelRequestDTO.UpdateHostelRequestDTO;

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

    /**
     * Cập nhật ảnh cho hostel (xóa ảnh cũ và thêm ảnh mới)
     */
    @IsOwner
    @PutMapping("/{hostelId}/images")
    public ApiResponse<HostelResponseDto> updateHostelImages(
            @PathVariable Long hostelId,
            @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
            @RequestParam(value = "keepImages", required = false) List<String> keepImages) {
        try {
            HostelResponseDto hostel = hostelService.updateHostelImages(hostelId, imageFiles, keepImages);
            return ApiResponse.<HostelResponseDto>builder()
                    .code(200)
                    .message("Images updated successfully")
                    .result(hostel)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to update images: " + e.getMessage());
        }
    }

    /**
     * Lấy danh sách tất cả hostel (public - cho mọi người xem, kể cả khách vãng lai)
     */
    @GetMapping("/tenant/detailsHostel")
    public ApiResponse<List<HostelResponseDto>> getAllHostelsForTenant() {
        List<HostelResponseDto> hostels = hostelService.getAllHostelsForTenant();
        return ApiResponse.<List<HostelResponseDto>>builder()
                .code(200)
                .message("Lấy chi tiết danh sách tất cả nhà trọ thành công")
                .result(hostels)
                .build();
    }

    /**
     * Owner lấy danh sách hostel của chính mình
     */
    @IsOwner
    @GetMapping("/owner/my-hostels")
    public ApiResponse<List<HostelResponseDto>> getMyHostels() {
        List<HostelResponseDto> hostels = hostelService.getHostelsByOwner();
        return ApiResponse.<List<HostelResponseDto>>builder()
                .code(200)
                .message("Lấy danh sách hostel của owner thành công")
                .result(hostels)
                .build();
    }

    /**
     * Lấy chi tiết 1 hostel theo ID
     */
    @GetMapping("/tenant/detailsHostel/{hostelId}")
    public ApiResponse<HostelResponseDto> getHostelById(@PathVariable Long hostelId) {
        HostelResponseDto hostel = hostelService.getHostelById(hostelId);
        return ApiResponse.<HostelResponseDto>builder()
                .code(200)
                .message("Lấy chi tiết hostel thành công")
                .result(hostel)
                .build();
    }

    /**
     * Owner cập nhật thông tin hostel của mình
     */
    @PutMapping("/{hostelId}")
    @IsOwner
    public ApiResponse<UpdateHostelResponseDTO> updateHostel(@PathVariable Long hostelId ,@RequestBody  @Valid UpdateHostelRequestDTO hostelRequestDTO) {
        try{
             UpdateHostelResponseDTO updatedHostel = hostelService.updateHostel(hostelId, hostelRequestDTO);
             return ApiResponse.<UpdateHostelResponseDTO>builder()
                .code(200)
                .message("Cập nhật hostel thành công")
                .result(updatedHostel)
                .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Cập nhật hostel thất bại: " + e.getMessage());
        }
       
    }
}
