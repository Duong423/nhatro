package com.example.nhatro.service;

import com.example.nhatro.dto.request.HostelRequestDTO.HostelRequestDto;
import com.example.nhatro.dto.response.HostelResponseDto;
import com.example.nhatro.entity.Hostel;

public interface HostelService {
    HostelResponseDto addHostel(HostelRequestDto hostelRequestDto);
    
    HostelResponseDto uploadHostelImages(Long hostelId, java.util.List<org.springframework.web.multipart.MultipartFile> imageFiles);
}
