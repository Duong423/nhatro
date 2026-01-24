package com.example.nhatro.service;

import com.example.nhatro.dto.request.HostelRequestDTO.HostelRequestDto;
import com.example.nhatro.dto.request.HostelRequestDTO.UpdateHostelRequestDTO;
import com.example.nhatro.dto.response.HostelResponseDto;
import com.example.nhatro.dto.response.UpdateHostelResponseDTO;
import com.example.nhatro.entity.Hostel;

import java.util.List;

public interface HostelService {
    HostelResponseDto addHostel(HostelRequestDto hostelRequestDto);
    
    HostelResponseDto uploadHostelImages(Long hostelId, java.util.List<org.springframework.web.multipart.MultipartFile> imageFiles);

    HostelResponseDto updateHostelImages(Long hostelId, java.util.List<org.springframework.web.multipart.MultipartFile> imageFiles, java.util.List<String> keepImages);

    List<HostelResponseDto> getAllHostelsForTenant();
    
    List<HostelResponseDto> getHostelsByOwner();
    
    HostelResponseDto getHostelById(Long hostelId);

    UpdateHostelResponseDTO updateHostel(Long hostelId, UpdateHostelRequestDTO updateHostelRequestDTO);
}

