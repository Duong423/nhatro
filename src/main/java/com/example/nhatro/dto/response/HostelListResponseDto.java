package com.example.nhatro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelListResponseDto {
    
    private long totalRooms;        // Tổng số phòng
    private long availableRooms;    // Số phòng còn trống (AVAILABLE)
    private long occupiedRooms;     // Số phòng đã thuê (FULL)
    private List<HostelResponseDto> hostels; // Danh sách hostel
}
