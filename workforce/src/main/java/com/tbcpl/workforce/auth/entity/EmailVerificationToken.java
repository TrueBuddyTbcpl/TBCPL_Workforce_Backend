// com/tbcpl/workforce/auth/entity/EmailVerificationToken.java
package com.tbcpl.workforce.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Stores email verification tokens for first-time login email verification.
 * Token expires in 24 hours. Only one active token per employee at a time.
 */
@Entity
@Table(name = "email_verification_tokens", indexes = {
        @Index(name = "idx_evtoken_token",       columnList = "token"),
        @Index(name = "idx_evtoken_employee_id",  columnList = "employee_id"),
        @Index(name = "idx_evtoken_used_expired", columnList = "is_used, expires_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "token", nullable = false, unique = true, length = 64)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used", nullable = false)   // ← DB column name stays the same ✅
    @Builder.Default
    private Boolean used = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return !used && !isExpired();
    }
}
