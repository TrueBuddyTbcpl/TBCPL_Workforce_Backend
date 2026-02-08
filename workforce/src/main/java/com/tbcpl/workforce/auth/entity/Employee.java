package com.tbcpl.workforce.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class for Employee table
 * HR and ADMIN can manage employees
 */
@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_emp_id", columnList = "emp_id"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_department_id", columnList = "department_id"),
        @Index(name = "idx_role_id", columnList = "role_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "emp_id", nullable = false, unique = true, length = 20)
    private String empId; // Format: 2026/001

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email; // Format: example.2026@gnsp.co.in

    @Column(name = "password", nullable = false)
    private String password; // BCrypt hashed

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "middle_name", length = 50)
    private String middleName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "last_password_change_date")
    private LocalDate lastPasswordChangeDate;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    /**
     * Get full name (first + middle + last)
     */
    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }

    /**
     * Check if password needs to be changed (after 2 months)
     */
    public boolean isPasswordExpired() {
        if (lastPasswordChangeDate == null) {
            return false; // First time login, no expiry
        }
        LocalDate expiryDate = lastPasswordChangeDate.plusMonths(2);
        return LocalDate.now().isAfter(expiryDate);
    }

    /**
     * Get days until password expires
     */
    public long getDaysUntilPasswordExpiry() {
        if (lastPasswordChangeDate == null) {
            return Long.MAX_VALUE; // No expiry for first time
        }
        LocalDate expiryDate = lastPasswordChangeDate.plusMonths(2);
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    /**
     * Check if password expiry warning should be shown (within 7 days)
     */
    public boolean shouldShowPasswordExpiryWarning() {
        long daysUntilExpiry = getDaysUntilPasswordExpiry();
        return daysUntilExpiry > 0 && daysUntilExpiry <= 7;
    }
}
