package com.devos.backend.auth.service;

import com.devos.backend.auth.dto.request.LoginRequest;
import com.devos.backend.auth.dto.request.RegisterRequest;
import com.devos.backend.auth.dto.response.AuthResponse;
import com.devos.backend.auth.dto.response.UserResponse;
import com.devos.backend.auth.entity.User;
import com.devos.backend.auth.enums.Role;
import com.devos.backend.auth.repository.UserRepository;
import com.devos.backend.auth.security.JwtService;
import com.devos.backend.common.dto.ApiResponse;
import com.devos.backend.common.exception.EmailAlreadyExistsException;
import com.devos.backend.common.exception.InvalidCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ApiResponse<Void> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.DEVELOPER)
                .build();

        userRepository.save(user);

        return ApiResponse.<Void>builder()
                .success(true)
                .message("User Registered Successfully")
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }


    public ApiResponse<AuthResponse> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid Email or Password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

            throw new InvalidCredentialsException("Invalid Email or Password");
        }

        if (!user.isActive()) {
            throw new InvalidCredentialsException(
                    "Account is inactive"
            );
        }

        user.setLastLogin(LocalDateTime.now());

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .message("Login Successful")
                .build();

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login Successful")
                .data(authResponse)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ApiResponse<UserResponse> getCurrentUser() {

        String userId = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal()
                .toString();

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() ->
                        new InvalidCredentialsException("User not found")
                );

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profilePicture(user.getProfilePicture())
                .role(user.getRole())
                .build();

        return ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Current user")
                .data(userResponse)
                .timestamp(LocalDateTime.now())
                .build();
    }
}