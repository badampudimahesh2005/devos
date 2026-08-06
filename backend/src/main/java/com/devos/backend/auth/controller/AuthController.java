package com.devos.backend.auth.controller;

import com.devos.backend.auth.dto.request.RegisterRequest;
import com.devos.backend.auth.service.AuthService;
import com.devos.backend.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {

        return authService.register(request);

    }
}