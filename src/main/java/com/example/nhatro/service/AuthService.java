package com.example.nhatro.service;

import com.example.nhatro.dto.request.AuthRequestDTO.LoginRequest;
import com.example.nhatro.dto.request.AuthRequestDTO.RefreshTokenRequest;
import com.example.nhatro.dto.request.AuthRequestDTO.RegisterRequest;
import com.example.nhatro.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
    
    AuthResponse refreshToken(RefreshTokenRequest request);
}