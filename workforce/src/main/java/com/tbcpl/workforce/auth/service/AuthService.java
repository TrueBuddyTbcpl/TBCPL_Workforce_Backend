package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.dto.request.LoginRequest;
import com.tbcpl.workforce.auth.dto.request.PasswordChangeRequest;
import com.tbcpl.workforce.auth.dto.request.PasswordResetRequest;
import com.tbcpl.workforce.auth.dto.response.LoginResponse;
import com.tbcpl.workforce.auth.dto.response.PasswordChangeResponse;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.EmployeeSession;
import com.tbcpl.workforce.auth.security.JwtUtil;
import com.tbcpl.workforce.common.constants.SecurityConstants;
import com.tbcpl.workforce.common.constants.ValidationMessages;
import com.tbcpl.workforce.common.exception.DuplicateSessionException;
import com.tbcpl.workforce.common.exception.InvalidCredentialsException;
import com.tbcpl.workforce.common.exception.PasswordExpiredException;
import com.tbcpl.workforce.common.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Service class for Authentication
 * Handles login, logout, and password management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final EmployeeService employeeService;
    private final EmployeeSessionService sessionService;
    private final LoginAttemptService loginAttemptService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;
    private final JwtUtil jwtUtil;

    /**
     * Login - authenticate employee and create session
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Processing login request for email: {}", request.getEmail());

        // Find employee by email
        Employee employee = employeeService.getEmployeeEntityByEmail(request.getEmail());

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            log.warn("Invalid password for email: {}", request.getEmail());
            loginAttemptService.logFailedLogin(
                    request.getEmail(),
                    request.getDeviceId(),
                    request.getIpAddress(),
                    request.getUserAgent(),
                    "Invalid password"
            );
            throw new InvalidCredentialsException(ValidationMessages.INVALID_CREDENTIALS);
        }

        // Check if employee is active
        if (!employee.getIsActive()) {
            log.warn("Inactive employee attempted login: {}", request.getEmail());
            loginAttemptService.logFailedLogin(
                    request.getEmail(),
                    request.getDeviceId(),
                    request.getIpAddress(),
                    request.getUserAgent(),
                    "Account is inactive"
            );
            throw new InvalidCredentialsException("Your account is inactive. Please contact HR.");
        }

        cleanupExpiredSessionsForEmployee(employee.getId());

        // Check if employee already has active session (multi-device prevention)
        if (sessionService.hasActiveSession(employee.getId())) {
            log.warn("Multi-device login attempt for employee: {}", employee.getEmpId());
            loginAttemptService.logBlockedLogin(
                    employee,
                    request.getDeviceId(),
                    request.getIpAddress(),
                    request.getUserAgent(),
                    SecurityConstants.DUPLICATE_SESSION_MESSAGE
            );
            throw new DuplicateSessionException(SecurityConstants.DUPLICATE_SESSION_MESSAGE);
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(employee);

        // Create session
        EmployeeSession session = sessionService.createSession(
                employee,
                token,
                request.getDeviceId(),
                request.getIpAddress()
        );

        // Update last login date
        employeeService.updateLastLoginDate(employee.getId());

        // Log successful login
        loginAttemptService.logSuccessfulLogin(
                employee,
                request.getDeviceId(),
                request.getIpAddress(),
                request.getUserAgent()
        );

        log.info("Login successful for employee: {}", employee.getEmpId());

        // Build response
        LoginResponse response = buildLoginResponse(employee, token);

        // Check password expiry and add warning if needed
        if (employee.shouldShowPasswordExpiryWarning()) {
            long daysLeft = employee.getDaysUntilPasswordExpiry();
            response.setPasswordExpiryWarning(
                    String.format(ValidationMessages.PASSWORD_EXPIRY_WARNING, daysLeft)
            );
        }

        return response;
    }

    @Transactional
    private void cleanupExpiredSessionsForEmployee(Long employeeId) {
        log.debug("Checking for expired sessions for employee ID: {}", employeeId);

        Optional<EmployeeSession> sessionOpt = sessionService.getActiveSession(employeeId);

        if (sessionOpt.isPresent()) {
            EmployeeSession session = sessionOpt.get();

            // Check if expired by timeout (8 hours)
            if (session.isExpired()) {
                log.info("Found expired session (timeout) for employee ID: {}, marking as expired", employeeId);
                session.markAsExpired();
                sessionService.saveSession(session); // You'll need to add this method
            }
            // Check if expired by date change
            else if (session.isDateChanged()) {
                log.info("Found expired session (date changed) for employee ID: {}, marking as expired", employeeId);
                session.markAsExpired();
                sessionService.saveSession(session); // You'll need to add this method
            }
        }
    }

    /**
     * Logout - invalidate session
     */
    @Transactional
    public void logout(String token) {
        log.info("Processing logout request");
        sessionService.logout(token);
        log.info("Logout successful");
    }

    /**
     * Change password - employee changes own password
     */
    /**
     * Change password - employee changes own password
     */
    @Transactional
    public PasswordChangeResponse changePassword(String email, PasswordChangeRequest request) {
        log.info("Processing password change for employee: {}", email);

        // Validate new password format
        passwordValidator.validatePassword(request.getNewPassword());

        // Check if new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("New password and confirm password do not match");
        }

        // Get employee
        Employee employee = employeeService.getEmployeeEntityByEmail(email);

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())) {
            log.warn("Current password incorrect for employee: {}", email);
            throw new InvalidCredentialsException(ValidationMessages.CURRENT_PASSWORD_INCORRECT);
        }

        // Check if new password is same as current password
        if (passwordEncoder.matches(request.getNewPassword(), employee.getPassword())) {
            throw new InvalidCredentialsException("New password cannot be the same as current password");
        }

        // Update password
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        employeeService.updateEmployeePassword(employee.getId(), encodedPassword);

        log.info("Password changed successfully for employee: {}", email);

        // Build response
        LocalDate nextChangeDate = LocalDate.now().plusMonths(SecurityConstants.PASSWORD_EXPIRY_MONTHS);
        return PasswordChangeResponse.builder()
                .message("Password changed successfully")
                .lastPasswordChangeDate(LocalDate.now())
                .nextPasswordChangeDate(nextChangeDate)
                .daysUntilExpiry((long) SecurityConstants.PASSWORD_EXPIRY_DAYS)
                .build();
    }

    /**
     * Reset password - HR/ADMIN resets employee password
     */
    @Transactional
    public void resetPassword(PasswordResetRequest request, String resetBy) {
        log.info("Processing password reset for employee: {}", request.getEmpId());

        // Validate new password format
        passwordValidator.validatePassword(request.getNewPassword());

        // Check if new password matches confirm password
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new InvalidCredentialsException("New password and confirm password do not match");
        }

        // Get employee by empId
        Employee employee = employeeService.getEmployeeEntityByEmail(request.getEmpId());

        // Update password
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employee.setLastPasswordChangeDate(LocalDate.now());
        employeeService.updateEmployee(
                employee.getId(),
                null,
                resetBy
        );

        // Force logout all sessions
        sessionService.logoutByEmployeeId(employee.getId());

        log.info("Password reset successfully for employee: {} by: {}", request.getEmpId(), resetBy);
    }

    /**
     * Build login response DTO
     */
    private LoginResponse buildLoginResponse(Employee employee, String token) {
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(SecurityConstants.JWT_EXPIRATION_MS)
                .empId(employee.getEmpId())
                .email(employee.getEmail())
                .fullName(employee.getFullName())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .departmentId(employee.getDepartment().getId())
                .departmentName(employee.getDepartment().getDepartmentName())
                .roleId(employee.getRole().getId())
                .roleName(employee.getRole().getRoleName())
                .passwordExpired(employee.isPasswordExpired())
                .daysUntilPasswordExpiry(employee.getDaysUntilPasswordExpiry())
                .build();
    }
}
