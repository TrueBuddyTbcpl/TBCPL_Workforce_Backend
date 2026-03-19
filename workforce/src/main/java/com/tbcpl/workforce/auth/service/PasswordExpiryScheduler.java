package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.auth.repository.PasswordResetTokenRepository;
import com.tbcpl.workforce.common.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordExpiryScheduler {

    private final EmployeeRepository        employeeRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordResetEmailService passwordResetEmailService;

    // Roles exempt from 60-day password expiry rule
    private static final Set<String> EXEMPT_ROLES = Set.of(
            RoleType.SUPER_ADMIN.getDbValue().toUpperCase(),
            RoleType.ADMIN.getDbValue().toUpperCase()
    );

    /**
     * Runs every day at 8:00 AM IST.
     * Sends warning email to non-admin employees whose password expires in ≤ 7 days.
     */
    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Kolkata")
    @Transactional(readOnly = true)
    public void sendPasswordExpiryWarnings() {
        log.info("⏰ Running password expiry check scheduler");

        // Fetch all active employees in batches
        int page = 0;
        int batchSize = 50;
        List<Employee> batch;

        do {
            batch = employeeRepository
                    .findByIsActiveTrue(PageRequest.of(page, batchSize))
                    .getContent();

            for (Employee emp : batch) {
                String role = emp.getRole().getRoleName().toUpperCase();
                if (EXEMPT_ROLES.contains(role)) continue;  // Skip ADMIN/SUPER_ADMIN

                if (emp.shouldShowPasswordExpiryWarning()) {
                    passwordResetEmailService.sendExpiryWarningEmail(emp);
                    log.info("📧 Expiry warning sent to: {} ({} days left)",
                            emp.getEmail(), emp.getDaysUntilPasswordExpiry());
                }
            }
            page++;
        } while (!batch.isEmpty());

        log.info("✅ Password expiry check COMPLETED");
    }

    /**
     * Runs every day at midnight — cleanup expired reset tokens.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Kolkata")
    public void cleanupExpiredTokens() {
        resetTokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("🧹 Expired password reset tokens cleaned up");
    }
}
