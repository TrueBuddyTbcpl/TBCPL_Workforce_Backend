package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.dto.response.LoginAttemptResponse;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.LoginAttemptLog;
import com.tbcpl.workforce.auth.repository.LoginAttemptLogRepository;
import com.tbcpl.workforce.common.enums.LoginAttemptStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for Login Attempt logging and monitoring
 * HR can view login attempts for security monitoring
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginAttemptService {

    private final LoginAttemptLogRepository loginAttemptRepository;

    /**
     * Log successful login attempt
     */
    @Transactional
    public void logSuccessfulLogin(Employee employee, String deviceId, String ipAddress, String userAgent) {
        log.info("Logging successful login for employee: {}", employee.getEmpId());

        LoginAttemptLog attemptLog = LoginAttemptLog.builder()
                .employee(employee)
                .email(employee.getEmail())
                .deviceIdentifier(deviceId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(LoginAttemptStatus.SUCCESS)
                .build();

        loginAttemptRepository.save(attemptLog);
    }

    /**
     * Log blocked login attempt (multi-device)
     */
    @Transactional
    public void logBlockedLogin(Employee employee, String deviceId, String ipAddress, String userAgent, String reason) {
        log.warn("Logging blocked login attempt for employee: {}", employee.getEmpId());

        LoginAttemptLog attemptLog = LoginAttemptLog.builder()
                .employee(employee)
                .email(employee.getEmail())
                .deviceIdentifier(deviceId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(LoginAttemptStatus.BLOCKED)
                .failureReason(reason)
                .build();

        loginAttemptRepository.save(attemptLog);
    }

    /**
     * Log failed login attempt (invalid credentials)
     */
    @Transactional
    public void logFailedLogin(String email, String deviceId, String ipAddress, String userAgent, String reason) {
        log.warn("Logging failed login attempt for email: {}", email);

        LoginAttemptLog attemptLog = LoginAttemptLog.builder()
                .employee(null) // Employee might not exist
                .email(email)
                .deviceIdentifier(deviceId)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .status(LoginAttemptStatus.FAILED)
                .failureReason(reason)
                .build();

        loginAttemptRepository.save(attemptLog);
    }

    /**
     * Get all login attempts with pagination
     * HR can use this
     */
    @Transactional(readOnly = true)
    public Page<LoginAttemptResponse> getAllLoginAttempts(Pageable pageable) {
        log.info("Fetching all login attempts");
        Page<LoginAttemptLog> attemptsPage = loginAttemptRepository.findAllByOrderByAttemptTimeDesc(pageable);
        return attemptsPage.map(this::mapToResponse);
    }

    /**
     * Get blocked login attempts with pagination
     * HR can use this for monitoring
     */
    @Transactional(readOnly = true)
    public Page<LoginAttemptResponse> getBlockedLoginAttempts(Pageable pageable) {
        log.info("Fetching blocked login attempts");
        Page<LoginAttemptLog> attemptsPage = loginAttemptRepository.findBlockedAttempts(pageable);
        return attemptsPage.map(this::mapToResponse);
    }

    /**
     * Get login attempts by employee
     * HR can use this
     */
    @Transactional(readOnly = true)
    public Page<LoginAttemptResponse> getLoginAttemptsByEmployeeId(Long employeeId, Pageable pageable) {
        log.info("Fetching login attempts for employee ID: {}", employeeId);
        Page<LoginAttemptLog> attemptsPage = loginAttemptRepository.findByEmployeeId(employeeId, pageable);
        return attemptsPage.map(this::mapToResponse);
    }

    /**
     * Get login attempts by email
     */
    @Transactional(readOnly = true)
    public Page<LoginAttemptResponse> getLoginAttemptsByEmail(String email, Pageable pageable) {
        log.info("Fetching login attempts for email: {}", email);
        Page<LoginAttemptLog> attemptsPage = loginAttemptRepository.findByEmailOrderByAttemptTimeDesc(email, pageable);
        return attemptsPage.map(this::mapToResponse);
    }

    /**
     * Get recent blocked attempts for employee (last 24 hours)
     */
    @Transactional(readOnly = true)
    public List<LoginAttemptLog> getRecentBlockedAttempts(Long employeeId) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return loginAttemptRepository.findRecentBlockedAttemptsByEmployee(employeeId, since);
    }

    /**
     * Count failed attempts by email in last X minutes (for rate limiting)
     */
    @Transactional(readOnly = true)
    public long countRecentFailedAttempts(String email, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return loginAttemptRepository.countFailedAttemptsByEmailSince(email, since);
    }

    /**
     * Scheduled task to clean up old login attempt logs
     * Runs daily at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * *") // Daily at 3 AM
    @Transactional
    public void deleteOldLoginAttempts() {
        log.info("Starting deletion of old login attempt logs");

        // Delete logs older than 90 days
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);
        int deletedCount = loginAttemptRepository.deleteOldLogs(cutoffDate);

        log.info("Deleted {} old login attempt logs", deletedCount);
    }

    /**
     * Get count of blocked attempts (for HR dashboard)
     */
    @Transactional(readOnly = true)
    public long countBlockedAttempts() {
        return loginAttemptRepository.countByStatus(LoginAttemptStatus.BLOCKED);
    }

    /**
     * Map LoginAttemptLog entity to LoginAttemptResponse DTO
     */
    private LoginAttemptResponse mapToResponse(LoginAttemptLog log) {
        return LoginAttemptResponse.builder()
                .id(log.getId())
                .empId(log.getEmployee() != null ? log.getEmployee().getEmpId() : null)
                .employeeName(log.getEmployee() != null ? log.getEmployee().getFullName() : "Unknown")
                .email(log.getEmail())
                .attemptTime(log.getAttemptTime())
                .deviceIdentifier(log.getDeviceIdentifier())
                .ipAddress(log.getIpAddress())
                .userAgent(log.getUserAgent())
                .status(log.getStatus())
                .statusDescription(log.getStatus().getDescription())
                .failureReason(log.getFailureReason())
                .build();
    }
}
