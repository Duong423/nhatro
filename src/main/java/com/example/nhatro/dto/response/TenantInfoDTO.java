package com.example.nhatro.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantInfoDTO {

    private Long tenantId;
    private String name;
    private String phone;
    private String email;
    private String cccd;

    // Thông tin phòng/hợp đồng liên quan
    private Long contractId;
    private String hostelName;
    private String roomCode;
}
