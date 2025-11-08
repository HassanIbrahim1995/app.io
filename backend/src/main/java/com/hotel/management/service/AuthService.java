package com.hotel.management.service;

import com.hotel.management.dto.request.LoginRequest;
import com.hotel.management.dto.request.RegisterRequest;
import com.hotel.management.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}
