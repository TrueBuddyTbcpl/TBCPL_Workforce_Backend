package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.request.LoginRequest;
import com.tbcpl.workforce.auth.dto.request.PasswordChangeRequest;
import com.tbcpl.workforce.auth.dto.request.PasswordResetRequest;
import com.tbcpl.workforce.auth.dto.response.LoginResponse;
import com.tbcpl.workforce.auth.dto.response.PasswordChangeResponse;
import com.tbcpl.workforce.auth.service.AuthService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication endpoints
 * Handles login, logout, password change, and password reset
 */
@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint
     * PUBLIC - No authentication required
     */
    @PostMapping(ApiEndpoints.AUTH_LOGIN)
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(
                ApiResponse.success("Login successful", response)
        );
    }

    /**
     * Logout endpoint
     * Requires authentication
     */
    @PostMapping(ApiEndpoints.AUTH_LOGOUT)
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        log.info("Logout request received");

        // Extract token from "Bearer <token>"
        String token = authHeader.substring(7);
        authService.logout(token);

        return ResponseEntity.ok(
                ApiResponse.success("Logout successful", null)
        );
    }

    /**
     * Change password endpoint
     * Employee changes their own password
     * Requires authentication
     */
    @PostMapping(ApiEndpoints.AUTH_CHANGE_PASSWORD)
    public ResponseEntity<ApiResponse<PasswordChangeResponse>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // Email from JWT
        log.info("Password change request for employee: {}", email);

        PasswordChangeResponse response = authService.changePassword(email, request);

        return ResponseEntity.ok(
                ApiResponse.success("Password changed successfully", response)
        );
    }

    /**
     * Reset password endpoint
     * HR or ADMIN resets employee password
     * Requires HR or ADMIN role
     */
    @PostMapping(ApiEndpoints.AUTH_RESET_PASSWORD)
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request,
            Authentication authentication
    ) {
        String resetBy = authentication.getName(); // Email of HR/ADMIN
        log.info("Password reset request for employee: {} by: {}", request.getEmpId(), resetBy);

        authService.resetPassword(request, resetBy);

        return ResponseEntity.ok(
                ApiResponse.success("Password reset successful. Employee must login with new password.", null)
        );
    }
}
