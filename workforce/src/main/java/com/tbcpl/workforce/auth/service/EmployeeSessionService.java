package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.EmployeeSession;
import com.tbcpl.workforce.auth.repository.EmployeeSessionRepository;
import com.tbcpl.workforce.common.constants.SecurityConstants;
import com.tbcpl.workforce.common.enums.SessionStatus;
import com.tbcpl.workforce.common.exception.DuplicateSessionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Employee Session management
 * Enforces single device login per employee
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeSessionService {

    private final EmployeeSessionRepository sessionRepository;

    /**
     * Check if employee has active session
     * Used during login to enforce single device policy
     */
    @Transactional(readOnly = true)
    public boolean hasActiveSession(Long employeeId) {
        return sessionRepository.hasActiveSession(employeeId);
    }

    /**
     * Get active session for employee
     */
    @Transactional(readOnly = true)
    public Optional<EmployeeSession> getActiveSession(Long employeeId) {
        return sessionRepository.findActiveSessionByEmployeeId(employeeId);
    }

    /**
     * Create new session for employee
     * Throws exception if active session exists (multi-device prevention)
     */
    @Transactional
    public EmployeeSession createSession(Employee employee, String token, String deviceId, String ipAddress) {
        log.info("Creating session for employee: {}", employee.getEmpId());

        // Check if employee already has active session
        if (hasActiveSession(employee.getId())) {
            log.warn("Employee {} already has an active session", employee.getEmpId());
            throw new DuplicateSessionException(SecurityConstants.DUPLICATE_SESSION_MESSAGE);
        }

        // Create new session
        EmployeeSession session = EmployeeSession.builder()
                .employee(employee)
                .sessionToken(token)
                .deviceIdentifier(deviceId)
                .ipAddress(ipAddress)
                .loginTime(LocalDateTime.now())
                .lastActivityTime(LocalDateTime.now())
                .isActive(true)
                .status(SessionStatus.ACTIVE)
                .build();

        EmployeeSession savedSession = sessionRepository.save(session);
        log.info("Session created successfully for employee: {}", employee.getEmpId());

        return savedSession;
    }

    /**
     * Update session activity time
     */
    @Transactional
    public void updateSessionActivity(String token) {
        sessionRepository.findActiveSessionByToken(token)
                .ifPresent(session -> {
                    session.setLastActivityTime(LocalDateTime.now());
                    sessionRepository.save(session);
                });
    }

    /**
     * Logout - deactivate session
     */
    @Transactional
    public void logout(String token) {
        log.info("Logging out session");
        sessionRepository.findActiveSessionByToken(token)
                .ifPresent(session -> {
                    session.markAsLoggedOut();
                    sessionRepository.save(session);
                    log.info("Session logged out successfully for employee: {}", session.getEmployee().getEmpId());
                });
    }

    /**
     * Logout by employee ID - deactivate all sessions
     */
    @Transactional
    public void logoutByEmployeeId(Long employeeId) {
        log.info("Logging out all sessions for employee ID: {}", employeeId);
        int count = sessionRepository.deactivateAllSessionsByEmployeeId(
                employeeId,
                SessionStatus.LOGGED_OUT,
                LocalDateTime.now()
        );
        log.info("Deactivated {} session(s) for employee ID: {}", count, employeeId);
    }

    /**
     * Force logout - used when new login occurs
     */
    @Transactional
    public void forceLogout(Long employeeId) {
        log.info("Force logging out sessions for employee ID: {}", employeeId);
        sessionRepository.findActiveSessionByEmployeeId(employeeId)
                .ifPresent(session -> {
                    session.markAsForceLogout();
                    sessionRepository.save(session);
                    log.info("Session force logged out for employee: {}", session.getEmployee().getEmpId());
                });
    }

    /**
     * Validate if session is active and not expired
     */
    @Transactional(readOnly = true)
    public boolean isSessionValid(String token) {
        Optional<EmployeeSession> sessionOpt = sessionRepository.findActiveSessionByToken(token);

        if (sessionOpt.isEmpty()) {
            return false;
        }

        EmployeeSession session = sessionOpt.get();

        // Check if session is expired (8 hours timeout)
        if (session.isExpired()) {
            log.warn("Session expired for token");
            return false;
        }

        // Check if date has changed (auto-logout feature)
        if (session.isDateChanged()) {
            log.warn("Session date changed for token");
            return false;
        }

        return true;
    }

    /**
     * Get session by token
     */
    @Transactional(readOnly = true)
    public Optional<EmployeeSession> getSessionByToken(String token) {
        return sessionRepository.findBySessionToken(token);
    }

    /**
     * Scheduled task to clean up expired sessions
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * *") // Every hour at minute 0
    @Transactional
    public void cleanupExpiredSessions() {
        log.info("Starting cleanup of expired sessions");

        // Find sessions expired by timeout (8 hours)
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(SecurityConstants.SESSION_TIMEOUT_HOURS);
        List<EmployeeSession> expiredSessions = sessionRepository.findExpiredSessions(expiryTime);

        expiredSessions.forEach(session -> {
            session.markAsExpired();
            sessionRepository.save(session);
        });

        log.info("Marked {} sessions as expired", expiredSessions.size());

        // Find sessions with changed date (auto-logout)
        List<EmployeeSession> dateChangedSessions = sessionRepository.findSessionsWithChangedDate();

        dateChangedSessions.forEach(session -> {
            session.markAsExpired();
            sessionRepository.save(session);
        });

        log.info("Auto-logged out {} sessions due to date change", dateChangedSessions.size());
    }

    /**
     * Scheduled task to delete old inactive sessions
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    @Transactional
    public void deleteOldInactiveSessions() {
        log.info("Starting deletion of old inactive sessions");

        // Delete sessions older than 30 days
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        int deletedCount = sessionRepository.deleteOldInactiveSessions(cutoffDate);

        log.info("Deleted {} old inactive sessions", deletedCount);
    }

    /**
     * Get all sessions for an employee (for admin/HR monitoring)
     */
    @Transactional(readOnly = true)
    public List<EmployeeSession> getEmployeeSessions(Employee employee) {
        return sessionRepository.findByEmployeeOrderByLoginTimeDesc(employee);
    }

    /**
     * Count active sessions
     */
    @Transactional(readOnly = true)
    public long countActiveSessions() {
        return sessionRepository.countByIsActiveTrue();
    }
}
