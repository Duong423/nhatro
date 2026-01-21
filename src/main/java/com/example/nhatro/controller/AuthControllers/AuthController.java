package com.example.nhatro.controller.AuthControllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nhatro.common.dto.response.ApiResponse;
import com.example.nhatro.dto.request.AuthRequestDTO.LoginRequest;
import com.example.nhatro.dto.request.AuthRequestDTO.RefreshTokenRequest;
import com.example.nhatro.dto.request.AuthRequestDTO.RegisterRequest;
import com.example.nhatro.dto.response.AuthResponse;
import com.example.nhatro.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ApiResponse.<AuthResponse>builder()
                    .code(HttpStatus.CREATED.value())
                    .message("Đăng ký thành công")
                    .result(response)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ApiResponse.<AuthResponse>builder()
                    .code(HttpStatus.OK.value())
                    .message("Đăng nhập thành công")
                    .result(response)
                    .build();
        } catch (RuntimeException e) {
            throw new RuntimeException("Email hoặc mật khẩu không chính xác");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
    }
}
