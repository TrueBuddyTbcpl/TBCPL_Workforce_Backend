package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.response.LoginAttemptResponse;
import com.tbcpl.workforce.auth.service.LoginAttemptService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Login Attempt monitoring
 * Only HR and ADMIN can access
 */
@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE)
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptController {

    private final LoginAttemptService loginAttemptService;

    /**
     * Get all login attempts with pagination
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.LOGIN_ATTEMPTS)
    public ResponseEntity<ApiResponse<Page<LoginAttemptResponse>>> getAllLoginAttempts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Get all login attempts - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<LoginAttemptResponse> attempts = loginAttemptService.getAllLoginAttempts(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Login attempts retrieved successfully", attempts)
        );
    }

    /**
     * Get blocked login attempts (multi-device attempts)
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.LOGIN_ATTEMPTS_BLOCKED)
    public ResponseEntity<ApiResponse<Page<LoginAttemptResponse>>> getBlockedLoginAttempts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Get blocked login attempts - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<LoginAttemptResponse> attempts = loginAttemptService.getBlockedLoginAttempts(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Blocked login attempts retrieved successfully", attempts)
        );
    }

    /**
     * Get login attempts by employee ID
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.LOGIN_ATTEMPTS_BY_EMPLOYEE)
    public ResponseEntity<ApiResponse<Page<LoginAttemptResponse>>> getLoginAttemptsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Get login attempts for employee ID: {}", employeeId);

        Pageable pageable = PageRequest.of(page, size);
        Page<LoginAttemptResponse> attempts = loginAttemptService.getLoginAttemptsByEmployeeId(employeeId, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Login attempts retrieved successfully", attempts)
        );
    }

    /**
     * Get count of blocked attempts (for dashboard)
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.LOGIN_ATTEMPTS + "/blocked/count")
    public ResponseEntity<ApiResponse<Long>> getBlockedAttemptsCount() {
        log.info("Get blocked attempts count");

        long count = loginAttemptService.countBlockedAttempts();

        return ResponseEntity.ok(
                ApiResponse.success("Blocked attempts count retrieved successfully", count)
        );
    }
}
