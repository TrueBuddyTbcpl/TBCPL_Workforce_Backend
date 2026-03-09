package com.tbcpl.workforce.auth.entity;

import com.tbcpl.workforce.common.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity class for Employee Session tracking
 * Enforces single device login per employee
 */
@Entity
@Table(name = "employee_sessions", indexes = {
        @Index(name = "idx_employee_id", columnList = "employee_id"),
        @Index(name = "idx_session_token", columnList = "session_token"),
        @Index(name = "idx_is_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "session_token", nullable = false, unique = true, length = 500)
    private String sessionToken; // JWT token

    @Column(name = "device_identifier", length = 255)
    private String deviceIdentifier; // Browser fingerprint or device ID

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "login_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime loginTime;

    @Column(name = "last_activity_time")
    @UpdateTimestamp
    private LocalDateTime lastActivityTime;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private SessionStatus status = SessionStatus.ACTIVE;

    /**
     * Check if session is expired (based on last activity)
     */
    public boolean isExpired() {
        if (!isActive) {
            return true;
        }
        LocalDateTime expiryTime = lastActivityTime.plusHours(8); // 8 hours timeout
        return LocalDateTime.now().isAfter(expiryTime);
    }

    /**
     * Check if session date has changed (auto-logout feature)
     */
    public boolean isDateChanged() {
        return !loginTime.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }

    /**
     * Mark session as logged out
     */
    public void markAsLoggedOut() {
        this.isActive = false;
        this.logoutTime = LocalDateTime.now();
        this.status = SessionStatus.LOGGED_OUT;
    }

    /**
     * Mark session as expired
     */
    public void markAsExpired() {
        this.isActive = false;
        this.status = SessionStatus.EXPIRED;
    }

    /**
     * Mark session as force logout (due to new login)
     */
    public void markAsForceLogout() {
        this.isActive = false;
        this.logoutTime = LocalDateTime.now();
        this.status = SessionStatus.FORCE_LOGOUT;
    }
}
