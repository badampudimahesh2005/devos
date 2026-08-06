package com.devos.backend.auth.service;

import com.devos.backend.auth.dto.RegisterRequest;
import com.devos.backend.auth.entity.User;
import com.devos.backend.auth.repository.UserRepository;
import com.devos.backend.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public ApiResponse<Void> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ApiResponse.<Void>builder()
                    .success(false)
                    .message("Email already exists")
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .role("DEVELOPER")
                .active(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("User Registered Successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}