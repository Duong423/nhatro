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

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /**
     * Endpoint tạo hostel kèm ảnh và dịch vụ (Manual Parsing for Stability)
     */
    @IsOwner
    @PostMapping("/create-with-images")
    public ApiResponse<HostelResponseDto> createHostelWithImages(jakarta.servlet.http.HttpServletRequest request) {
        try {
            System.out.println("=== START CREATE HOSTEL REQUEST ===");
            
            // Check multipart
            if (!(request instanceof org.springframework.web.multipart.MultipartHttpServletRequest)) {
                return ApiResponse.<HostelResponseDto>builder()
                    .code(400)
                    .message("Request must be multipart/form-data")
                    .result(null)
                    .build();
            }
            
            org.springframework.web.multipart.MultipartHttpServletRequest multipartRequest = 
                (org.springframework.web.multipart.MultipartHttpServletRequest) request;

            
            // Extract core fields
            String title = multipartRequest.getParameter("title");
            String address = multipartRequest.getParameter("address");
            String priceStr = multipartRequest.getParameter("price");
            String description = multipartRequest.getParameter("description");
            
            // Basic validation
            if (title == null || address == null || priceStr == null) {
                 return ApiResponse.<HostelResponseDto>builder()
                    .code(400)
                    .message("Missing required fields (title, address, price)")
                    .result(null)
                    .build();
            }

            HostelRequestDto dto = new HostelRequestDto();
            dto.setTitle(title);
            dto.setAddress(address);
            dto.setPrice(Double.parseDouble(priceStr));
            dto.setDescription(description);
            
            dto.setDistrict(multipartRequest.getParameter("district"));
            dto.setCity(multipartRequest.getParameter("city"));
            
            String areaStr = multipartRequest.getParameter("area");
            if (areaStr != null && !areaStr.isEmpty()) dto.setArea(Double.parseDouble(areaStr));
            
            String roomCountStr = multipartRequest.getParameter("roomCount");
            if (roomCountStr != null && !roomCountStr.isEmpty()) dto.setRoomCount(Integer.parseInt(roomCountStr));
            
            String maxOccupancyStr = multipartRequest.getParameter("maxOccupancy");
            if (maxOccupancyStr != null && !maxOccupancyStr.isEmpty()) dto.setMaxOccupancy(Integer.parseInt(maxOccupancyStr));
            
            dto.setRoomType(multipartRequest.getParameter("roomType"));
            dto.setAmenities(multipartRequest.getParameter("amenities"));
            
            // Set unit prices
            dto.setElecUnitPrice(multipartRequest.getParameter("elecUnitPrice"));
            dto.setWaterUnitPrice(multipartRequest.getParameter("waterUnitPrice"));
            dto.setWifiUnitPrice(multipartRequest.getParameter("wifiUnitPrice"));
            dto.setParkingUnitPrice(multipartRequest.getParameter("parkingUnitPrice"));
            dto.setTrashUnitPrice(multipartRequest.getParameter("trashUnitPrice"));
            
            dto.setImageFiles(multipartRequest.getFiles("imageFiles"));

            HostelResponseDto result = hostelService.addHostelWithImages(dto);
            
            
            return ApiResponse.<HostelResponseDto>builder()
                    .code(201)
                    .message("Hostel created successfully")
                    .result(result)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.<HostelResponseDto>builder()
                    .code(400)
                    .message("Error creating hostel: " + e.getMessage())
                    .result(null)
                    .build();
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
