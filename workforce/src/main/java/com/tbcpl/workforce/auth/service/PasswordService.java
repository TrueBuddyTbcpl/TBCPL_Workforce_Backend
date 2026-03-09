package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.PasswordResetToken;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.auth.repository.PasswordResetTokenRepository;
import com.tbcpl.workforce.common.enums.RoleType;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {

    private final EmployeeRepository           employeeRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder              passwordEncoder;
    private final PasswordValidator            passwordValidator;
    private final PasswordResetEmailService    passwordResetEmailService;

    private static final int RESET_TOKEN_EXPIRY_HOURS = 1;

    // ─────────────────────────────────────────────────────────────────────────
    // CHANGE PASSWORD (knows current password — self only)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void changePassword(String empId, String currentPassword,
                               String newPassword, String confirmPassword) {
        validateNewPasswordMatch(newPassword, confirmPassword);
        passwordValidator.validatePassword(newPassword);

        Employee employee = findByEmpId(empId);

        if (!passwordEncoder.matches(currentPassword, employee.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (passwordEncoder.matches(newPassword, employee.getPassword())) {
            throw new IllegalArgumentException(
                    "New password must be different from the current password.");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setLastPasswordChangeDate(LocalDate.now());
        employeeRepository.save(employee);
        log.info("Password changed via current-password flow for empId: {}", empId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SEND RESET LINK (self — forgot password or expiry notification)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void sendPasswordResetLink(String empId) {
        Employee employee = findByEmpId(empId);

        // Invalidate any existing unused tokens
        resetTokenRepository.deleteUnusedTokensByEmployee(employee.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .employee(employee)
                .expiresAt(LocalDateTime.now().plusHours(RESET_TOKEN_EXPIRY_HOURS))
                .used(false)
                .build();
        resetTokenRepository.save(resetToken);

        passwordResetEmailService.sendResetEmail(employee, token);
        log.info("Password reset link sent to: {}", employee.getEmail());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CONFIRM RESET (user clicks link in email — no current password needed)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void confirmPasswordReset(String token, String newPassword, String confirmPassword) {
        validateNewPasswordMatch(newPassword, confirmPassword);
        passwordValidator.validatePassword(newPassword);

        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reset link is invalid or has already been used."));

        if (Boolean.TRUE.equals(resetToken.getUsed())) {
            throw new IllegalStateException("This reset link has already been used.");
        }
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Reset link has expired. Please request a new one.");
        }

        Employee employee = resetToken.getEmployee();
        if (passwordEncoder.matches(newPassword, employee.getPassword())) {
            throw new IllegalArgumentException(
                    "New password must be different from the current password.");
        }

        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setLastPasswordChangeDate(LocalDate.now());
        employeeRepository.save(employee);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
        log.info("Password reset confirmed for empId: {}", employee.getEmpId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN RESET (admin sets new password for any employee — no token needed)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void adminResetPassword(Long targetEmployeeId, String newPassword,
                                   String confirmPassword, String adminEmpId) {
        validateNewPasswordMatch(newPassword, confirmPassword);
        passwordValidator.validatePassword(newPassword);

        Employee admin = findByEmpId(adminEmpId);
        assertAdminRole(admin);

        Employee target = employeeRepository.findById(targetEmployeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + targetEmployeeId));

        target.setPassword(passwordEncoder.encode(newPassword));
        target.setLastPasswordChangeDate(LocalDate.now());
        employeeRepository.save(target);
        log.info("Admin [{}] reset password for employee [{}]",
                adminEmpId, target.getEmpId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private Employee findByEmpId(String empId) {
        return employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found: " + empId));
    }

    private void validateNewPasswordMatch(String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException(
                    "New password and confirm password do not match.");
        }
    }

    private void assertAdminRole(Employee employee) {
        String role = employee.getRole().getRoleName().toUpperCase();
        if (!role.equals(RoleType.SUPER_ADMIN.getDbValue().toUpperCase())
                && !role.equals(RoleType.ADMIN.getDbValue().toUpperCase())) {
            throw new IllegalArgumentException(
                    "Only ADMIN or SUPER_ADMIN can perform this action.");
        }
    }
}
