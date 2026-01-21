package com.example.nhatro.dto.request.AuthRequestDTO;

import com.example.nhatro.enums.UserRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleRequest {
    @NotNull(message = "User ID không được để trống")
    private Long userId;
    
    @NotNull(message = "Role không được để trống")
    private UserRole role;
}
