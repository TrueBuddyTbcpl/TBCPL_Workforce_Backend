// com/tbcpl/workforce/auth/entity/Employee.java
package com.tbcpl.workforce.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_emp_id",        columnList = "emp_id"),
        @Index(name = "idx_email",          columnList = "email"),
        @Index(name = "idx_department_id",  columnList = "department_id"),
        @Index(name = "idx_role_id",        columnList = "role_id"),
        @Index(name = "idx_reporting_mgr",  columnList = "reporting_manager_id"),
        @Index(name = "idx_email_verified", columnList = "email_verified")
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
    private String empId;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

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

    /**
     * Self-referencing FK: reporting manager must be an active employee
     * with role SUPER_ADMIN / ADMIN / MANAGER / HR_MANAGER
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_id")
    private Employee reportingManager;

    /** Cloudinary secure URL of profile photo */
    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    /** Cloudinary public_id for deletion */
    @Column(name = "profile_photo_public_id", length = 255)
    private String profilePhotoPublicId;

    /**
     * Email verification flag.
     * false = not yet verified (first-time login will trigger verification mail)
     * true  = verified, user can access their dashboard
     */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "last_password_change_date")
    private LocalDate lastPasswordChangeDate;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    // ─── Computed helpers ───────────────────────────────────────────────────

    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if (middleName != null && !middleName.trim().isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        return fullName.append(" ").append(lastName).toString();
    }

    public boolean isPasswordExpired() {
        if (lastPasswordChangeDate == null) return false;
        return LocalDate.now().isAfter(lastPasswordChangeDate.plusMonths(2));
    }

    public long getDaysUntilPasswordExpiry() {
        if (lastPasswordChangeDate == null) return Long.MAX_VALUE;
        LocalDate expiry = lastPasswordChangeDate.plusMonths(2);
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiry);
    }

    public boolean shouldShowPasswordExpiryWarning() {
        long days = getDaysUntilPasswordExpiry();
        return days > 0 && days <= 7;
    }

    /** True if this employee needs email verification before dashboard access */
    public boolean requiresEmailVerification() {
        return Boolean.FALSE.equals(emailVerified);
    }
}
