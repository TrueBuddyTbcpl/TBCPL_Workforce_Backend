package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.request.AdminPasswordResetRequest;
import com.tbcpl.workforce.auth.dto.request.PasswordChangeRequest;
import com.tbcpl.workforce.auth.dto.request.PasswordResetConfirmRequest;
import com.tbcpl.workforce.auth.service.PasswordService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE + ApiEndpoints.PASSWORD_BASE)
@RequiredArgsConstructor
@Slf4j
public class PasswordController {

    private final PasswordService passwordService;

    /**
     * POST /api/v1/auth/password/change
     * Self: change password using current password
     * Works for ALL roles including ADMIN/SUPER_ADMIN
     */
    @PostMapping("/change")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody PasswordChangeRequest request,
            Authentication authentication
    ) {
        String empId = authentication.getName();
        log.info("Change password request for: {}", empId);
        passwordService.changePassword(
                empId,
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    /**
     * POST /api/v1/auth/password/reset-request
     * Self: send reset link to own email
     * Works for ALL roles
     */
    @PostMapping("/reset-request")
    public ResponseEntity<ApiResponse<String>> requestPasswordReset(
            Authentication authentication
    ) {
        String empId = authentication.getName();
        log.info("Password reset link requested for: {}", empId);
        passwordService.sendPasswordResetLink(empId);
        return ResponseEntity.ok(
                ApiResponse.success("Reset link sent to your registered email", null));
    }

    /**
     * POST /api/v1/auth/password/reset-confirm
     * PUBLIC: confirm reset via token from email link
     * No authentication required
     */
    @PostMapping("/reset-confirm")
    public ResponseEntity<ApiResponse<String>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest request
    ) {
        log.info("Confirming password reset via token");
        passwordService.confirmPasswordReset(
                request.getToken(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    /**
     * POST /api/v1/auth/password/admin-reset
     * ADMIN / SUPER_ADMIN only
     * Reset any employee's password directly — no token needed
     */
    @PostMapping("/admin-reset")
    public ResponseEntity<ApiResponse<String>> adminResetPassword(
            @Valid @RequestBody AdminPasswordResetRequest request,
            Authentication authentication
    ) {
        String adminEmpId = authentication.getName();
        log.info("Admin [{}] resetting password for employee ID: {}",
                adminEmpId, request.getEmployeeId());
        passwordService.adminResetPassword(
                request.getEmployeeId(),
                request.getNewPassword(),
                request.getConfirmPassword(),
                adminEmpId
        );
        return ResponseEntity.ok(
                ApiResponse.success("Employee password reset successfully", null));
    }
}
