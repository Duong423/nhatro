package com.example.nhatro.dto.request.ServiceRequestDTO;

import com.example.nhatro.enums.ServiceUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho service khi tạo/cập nhật hostel
 * Có thể là service mới hoặc chọn từ service có sẵn
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceInHostelDto {
    
    // Nếu có serviceId thì chọn service có sẵn
    private Long serviceId;
    
    // Nếu không có serviceId thì tạo service mới với các thông tin sau
    private String serviceName;
    private BigDecimal price;
    private ServiceUnit unit;
    
    /**
     * Kiểm tra xem đây là service có sẵn hay service mới
     */
    public boolean isExistingService() {
        return serviceId != null;
    }
}
