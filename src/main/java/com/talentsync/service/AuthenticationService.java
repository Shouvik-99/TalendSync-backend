package com.talentsync.service;

import com.talentsync.dto.auth.AuthenticationRequest;
import com.talentsync.dto.auth.AuthenticationResponse;
import com.talentsync.dto.auth.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    
    /**
     * Register a new user
     * 
     * @param request Registration details
     * @return Authentication response with tokens
     */
    AuthenticationResponse register(RegisterRequest request);
    
    /**
     * Authenticate an existing user
     * 
     * @param request Authentication credentials
     * @return Authentication response with tokens
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);
    
    /**
     * Refresh the access token using a refresh token
     * 
     * @param request HTTP request containing refresh token
     * @param response HTTP response to set new tokens
     * @throws IOException If there's an issue writing the response
     */
    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
