package com.tbcpl.workforce.auth.repository;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.LoginAttemptLog;
import com.tbcpl.workforce.common.enums.LoginAttemptStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for LoginAttemptLog entity
 */
@Repository
public interface LoginAttemptLogRepository extends JpaRepository<LoginAttemptLog, Long> {

    /**
     * Find all login attempts ordered by time (most recent first)
     */
    Page<LoginAttemptLog> findAllByOrderByAttemptTimeDesc(Pageable pageable);

    /**
     * Find login attempts by employee
     */
    Page<LoginAttemptLog> findByEmployeeOrderByAttemptTimeDesc(Employee employee, Pageable pageable);

    /**
     * Find login attempts by employee ID
     */
    @Query("SELECT l FROM LoginAttemptLog l WHERE l.employee.id = :employeeId ORDER BY l.attemptTime DESC")
    Page<LoginAttemptLog> findByEmployeeId(@Param("employeeId") Long employeeId, Pageable pageable);

    /**
     * Find login attempts by email
     */
    Page<LoginAttemptLog> findByEmailOrderByAttemptTimeDesc(String email, Pageable pageable);

    /**
     * Find login attempts by status
     */
    Page<LoginAttemptLog> findByStatusOrderByAttemptTimeDesc(LoginAttemptStatus status, Pageable pageable);

    /**
     * Find blocked login attempts (for HR monitoring)
     */
    @Query("SELECT l FROM LoginAttemptLog l WHERE l.status = 'BLOCKED' ORDER BY l.attemptTime DESC")
    Page<LoginAttemptLog> findBlockedAttempts(Pageable pageable);

    /**
     * Find failed login attempts (for security monitoring)
     */
    @Query("SELECT l FROM LoginAttemptLog l WHERE l.status IN ('FAILED', 'BLOCKED') ORDER BY l.attemptTime DESC")
    Page<LoginAttemptLog> findFailedAttempts(Pageable pageable);

    /**
     * Find login attempts within date range
     */
    @Query("SELECT l FROM LoginAttemptLog l WHERE l.attemptTime BETWEEN :startDate AND :endDate ORDER BY l.attemptTime DESC")
    Page<LoginAttemptLog> findByAttemptTimeBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * Find recent blocked attempts by employee (last X hours)
     */
    @Query("SELECT l FROM LoginAttemptLog l WHERE l.employee.id = :employeeId AND l.status = 'BLOCKED' " +
            "AND l.attemptTime > :sinceTime ORDER BY l.attemptTime DESC")
    List<LoginAttemptLog> findRecentBlockedAttemptsByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("sinceTime") LocalDateTime sinceTime
    );

    /**
     * Count failed attempts by email in time window (for rate limiting)
     */
    @Query("SELECT COUNT(l) FROM LoginAttemptLog l WHERE l.email = :email " +
            "AND l.status = 'FAILED' AND l.attemptTime > :sinceTime")
    long countFailedAttemptsByEmailSince(
            @Param("email") String email,
            @Param("sinceTime") LocalDateTime sinceTime
    );

    /**
     * Count blocked attempts by employee
     */
    long countByEmployeeAndStatus(Employee employee, LoginAttemptStatus status);

    /**
     * Count all blocked attempts (for HR dashboard)
     */
    long countByStatus(LoginAttemptStatus status);

    /**
     * Clean up old logs (older than X days)
     */
    @Query("DELETE FROM LoginAttemptLog l WHERE l.attemptTime < :cutoffDate")
    int deleteOldLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
}
