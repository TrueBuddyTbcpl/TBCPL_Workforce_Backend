package com.tbcpl.workforce.auth.entity;

import com.tbcpl.workforce.common.enums.LoginAttemptStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class for Login Attempt logging
 * HR can view login attempts for monitoring
 */
@Entity
@Table(name = "login_attempt_logs", indexes = {
        @Index(name = "idx_employee_id", columnList = "employee_id"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_attempt_time", columnList = "attempt_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttemptLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee; // Null if employee not found

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "attempt_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime attemptTime;

    @Column(name = "device_identifier", length = 255)
    private String deviceIdentifier;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LoginAttemptStatus status;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "user_agent", length = 500)
    private String userAgent; // Browser/device info

    /**
     * Check if attempt was blocked due to duplicate session
     */
    public boolean isBlocked() {
        return status == LoginAttemptStatus.BLOCKED;
    }

    /**
     * Check if attempt was successful
     */
    public boolean isSuccess() {
        return status == LoginAttemptStatus.SUCCESS;
    }

    /**
     * Check if attempt failed
     */
    public boolean isFailed() {
        return status == LoginAttemptStatus.FAILED;
    }
}
