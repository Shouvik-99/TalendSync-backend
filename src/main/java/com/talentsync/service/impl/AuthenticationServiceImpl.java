package com.talentsync.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.talentsync.dto.auth.AuthenticationRequest;
import com.talentsync.dto.auth.AuthenticationResponse;
import com.talentsync.dto.auth.RegisterRequest;
import com.talentsync.model.Role;
import com.talentsync.model.User;
import com.talentsync.repository.UserRepository;
import com.talentsync.security.JwtService;
import com.talentsync.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Determine user role, default to USER if not specified
        Role userRole;
        try {
            userRole = request.getRole() != null ? 
                Role.valueOf(request.getRole().toUpperCase()) : Role.USER;
        } catch (IllegalArgumentException e) {
            userRole = Role.USER;
        }

        // Create new user
        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .build();
        
        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate with Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        // If no exception was thrown, user is authenticated
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generate tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String refreshToken;
        final String userEmail;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        
        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow();
                    
            if (jwtService.isTokenValid(refreshToken, user)) {
                String accessToken = jwtService.generateToken(user);
                
                AuthenticationResponse authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .userId(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .build();
                
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
