package com.example.nhatro.dto.response;

import com.example.nhatro.enums.ServiceUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDto {
    
    private Long serviceId;
    private Long hostelId;
    private String hostelName;
    private String serviceName;
    private BigDecimal price;
    private ServiceUnit unit;
    private LocalDateTime createdAt;
}
