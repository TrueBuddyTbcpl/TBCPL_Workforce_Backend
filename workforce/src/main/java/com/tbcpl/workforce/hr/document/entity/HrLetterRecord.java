package com.tbcpl.workforce.hr.document.entity;

import com.tbcpl.workforce.hr.document.entity.enums.LetterType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_letter_records",
        indexes = {
                @Index(name = "idx_letter_emp_id",      columnList = "emp_id"),
                @Index(name = "idx_letter_type",        columnList = "letter_type"),
                @Index(name = "idx_letter_issued_date", columnList = "issued_date"),
                @Index(name = "idx_letter_is_active",   columnList = "is_active"),
                @Index(name = "idx_letter_ref_no",      columnList = "reference_number", unique = true)
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrLetterRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auto-generated reference e.g. TBCPL/OL/2026/001
    @Column(name = "reference_number", nullable = false, unique = true, length = 50)
    private String referenceNumber;

    // Cross-dept reference — no JPA join
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Enumerated(EnumType.STRING)
    @Column(name = "letter_type", nullable = false, length = 50)
    private LetterType letterType;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    // Letter body content (can be HTML/plain text)
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // URL of generated/uploaded PDF
    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    // Who issued the letter — HR staff
    @Column(name = "issued_by", length = 100)
    private String issuedBy;

    // Acknowledged by employee?
    @Column(name = "is_acknowledged", nullable = false)
    @Builder.Default
    private Boolean isAcknowledged = false;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "remarks", length = 500)
    private String remarks;

    @Column(name = "is_active", nullable = false)
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
}