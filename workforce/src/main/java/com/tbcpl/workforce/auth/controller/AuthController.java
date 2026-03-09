package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.ResendVerificationRequest;
import com.tbcpl.workforce.auth.dto.request.LoginRequest;
import com.tbcpl.workforce.auth.dto.request.PasswordChangeRequest;
import com.tbcpl.workforce.auth.dto.request.PasswordResetRequest;
import com.tbcpl.workforce.auth.dto.response.EmployeeResponse;
import com.tbcpl.workforce.auth.dto.response.LoginResponse;
import com.tbcpl.workforce.auth.dto.response.PasswordChangeResponse;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.service.AuthService;
import com.tbcpl.workforce.auth.service.EmailVerificationService;
import com.tbcpl.workforce.auth.service.EmployeeService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Authentication endpoints
 */
@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE)
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService              authService;
    private final EmployeeService          employeeService;
    private final EmailVerificationService emailVerificationService;

    /**
     * POST /api/v1/auth/login
     */
    @PostMapping(ApiEndpoints.AUTH_LOGIN)
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("Login request for: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    /**
     * POST /api/v1/auth/logout
     */
    @PostMapping(ApiEndpoints.AUTH_LOGOUT)
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        log.info("Logout request");
        String token = authHeader.substring(7);
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }

    /**
     * POST /api/v1/auth/change-password
     */
    @PostMapping(ApiEndpoints.AUTH_CHANGE_PASSWORD)
    public ResponseEntity<ApiResponse<PasswordChangeResponse>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication
    ) {
        String email = employeeService
                .getEmployeeEntityByEmpId(authentication.getName()).getEmail();
        log.info("Change password request for empId: {}", authentication.getName());
        PasswordChangeResponse response = authService.changePassword(email, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", response));
    }

    /**
     * POST /api/v1/auth/reset-password
     * HR / ADMIN only
     */
    @PostMapping(ApiEndpoints.AUTH_RESET_PASSWORD)
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody PasswordResetRequest request,
            Authentication authentication
    ) {
        String resetBy = authentication.getName();
        log.info("Reset password request for empId: {} by: {}", request.getEmpId(), resetBy);
        authService.resetPassword(request, resetBy);
        return ResponseEntity.ok(
                ApiResponse.success("Password reset successful. Employee must login with new password.", null));
    }

    /**
     * GET /api/v1/auth/profile
     */
    @GetMapping(ApiEndpoints.AUTH_PROFILE)
    public ResponseEntity<ApiResponse<EmployeeResponse>> getCurrentEmployeeProfile(
            Authentication authentication
    ) {
        String empId = authentication.getName();
        log.info("Fetching profile for empId: {}", empId);
        EmployeeResponse response = employeeService.getEmployeeByEmpId(empId);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved successfully", response));
    }

    /**
     * GET /api/v1/auth/verify-email?token=...
     * PUBLIC — no JWT required
     */
    @GetMapping(ApiEndpoints.AUTH_VERIFY_EMAIL)
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @RequestParam("token") String token
    ) {
        log.info("Email verification attempt");
        emailVerificationService.verifyToken(token, employeeService);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Email verified successfully! Please login to access your dashboard.", null));
    }

    /**
     * POST /api/v1/auth/resend-verification/{empId}
     * ADMIN / SUPER_ADMIN only — resend by empId (path variable)
     */
    @PostMapping(ApiEndpoints.AUTH_RESEND_VERIFY)
    public ResponseEntity<ApiResponse<String>> resendVerification(
            @PathVariable String empId
    ) {
        log.info("Resend verification for empId: {}", empId);
        Employee emp = employeeService.getEmployeeEntityByEmpId(empId);
        emailVerificationService.resendVerificationEmail(emp);
        return ResponseEntity.ok(
                ApiResponse.success("Verification email resent to: " + emp.getEmail(), null));
    }

    /**
     * POST /api/v1/auth/resend-verification
     * PUBLIC — resend by email address in request body.
     * FIX 1: Delegate to employeeService.resendVerificationEmail(String) which
     *         resolves the Employee internally — emailVerificationService.resendVerificationEmail
     *         accepts Employee, NOT String.
     * FIX 2: request.getEmail() — ResendVerificationRequest is a POJO, not a record.
     * FIX 3: ApiResponse.success requires two arguments (message, data).
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        log.info("Resend verification email for: {}", request.getEmail());
        employeeService.resendVerificationEmail(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Verification email resent successfully", null));
    }
}
