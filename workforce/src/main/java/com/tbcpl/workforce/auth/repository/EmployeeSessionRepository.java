package com.tbcpl.workforce.auth.repository;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.EmployeeSession;
import com.tbcpl.workforce.common.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for EmployeeSession entity
 */
@Repository
public interface EmployeeSessionRepository extends JpaRepository<EmployeeSession, Long> {

    /**
     * Find active session by employee
     */
    @Query("SELECT s FROM EmployeeSession s WHERE s.employee.id = :employeeId AND s.isActive = true")
    Optional<EmployeeSession> findActiveSessionByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Find session by token
     */
    Optional<EmployeeSession> findBySessionToken(String sessionToken);

    /**
     * Find active session by token
     */
    @Query("SELECT s FROM EmployeeSession s WHERE s.sessionToken = :token AND s.isActive = true")
    Optional<EmployeeSession> findActiveSessionByToken(@Param("token") String token);

    /**
     * Check if employee has active session
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM EmployeeSession s " +
            "WHERE s.employee.id = :employeeId AND s.isActive = true")
    boolean hasActiveSession(@Param("employeeId") Long employeeId);

    /**
     * Find all active sessions by employee
     */
    List<EmployeeSession> findByEmployeeAndIsActiveTrue(Employee employee);

    /**
     * Find all sessions by employee
     */
    List<EmployeeSession> findByEmployeeOrderByLoginTimeDesc(Employee employee);

    /**
     * Deactivate all active sessions for an employee
     */
    @Modifying
    @Query("UPDATE EmployeeSession s SET s.isActive = false, s.status = :status, s.logoutTime = :logoutTime " +
            "WHERE s.employee.id = :employeeId AND s.isActive = true")
    int deactivateAllSessionsByEmployeeId(
            @Param("employeeId") Long employeeId,
            @Param("status") SessionStatus status,
            @Param("logoutTime") LocalDateTime logoutTime
    );

    /**
     * Deactivate specific session
     */
    @Modifying
    @Query("UPDATE EmployeeSession s SET s.isActive = false, s.status = :status, s.logoutTime = :logoutTime " +
            "WHERE s.id = :sessionId")
    int deactivateSession(
            @Param("sessionId") Long sessionId,
            @Param("status") SessionStatus status,
            @Param("logoutTime") LocalDateTime logoutTime
    );

    /**
     * Find expired sessions (last activity > X hours ago)
     */
    @Query("SELECT s FROM EmployeeSession s WHERE s.isActive = true AND s.lastActivityTime < :expiryTime")
    List<EmployeeSession> findExpiredSessions(@Param("expiryTime") LocalDateTime expiryTime);

    /**
     * Find sessions with changed date (for auto-logout)
     */
    @Query("SELECT s FROM EmployeeSession s WHERE s.isActive = true AND DATE(s.loginTime) < CURRENT_DATE")
    List<EmployeeSession> findSessionsWithChangedDate();

    /**
     * Clean up old inactive sessions (older than X days)
     */
    @Modifying
    @Query("DELETE FROM EmployeeSession s WHERE s.isActive = false AND s.logoutTime < :cutoffDate")
    int deleteOldInactiveSessions(@Param("cutoffDate") LocalDateTime cutoffDate);

    /**
     * Count active sessions
     */
    long countByIsActiveTrue();

    /**
     * Count total sessions by employee
     */
    long countByEmployee(Employee employee);
}
