package com.devos.backend.auth.controller;

import com.devos.backend.auth.dto.request.LoginRequest;
import com.devos.backend.auth.dto.request.RegisterRequest;
import com.devos.backend.auth.dto.response.AuthResponse;
import com.devos.backend.auth.dto.response.UserResponse;
import com.devos.backend.auth.service.AuthService;
import com.devos.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {

        return  ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {

        return ResponseEntity.ok(
                authService.getCurrentUser()
        );
    }

}