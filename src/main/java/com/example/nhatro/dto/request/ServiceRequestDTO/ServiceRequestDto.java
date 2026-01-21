package com.example.nhatro.dto.request.ServiceRequestDTO;

import com.example.nhatro.enums.ServiceUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRequestDto {
    
    @NotNull(message = "Hostel ID không được để trống")
    private Long hostelId;
    
    @NotBlank(message = "Tên dịch vụ không được để trống")
    private String serviceName;
    
    @NotNull(message = "Giá dịch vụ không được để trống")
    @Positive(message = "Giá dịch vụ phải lớn hơn 0")
    private BigDecimal price;
    
    @NotNull(message = "Đơn vị tính không được để trống")
    private ServiceUnit unit;
}
