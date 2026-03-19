package com.tbcpl.workforce.grnd_operation.entity;

import com.tbcpl.workforce.grnd_operation.entity.enums.LoaStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "loa",
        indexes = {
                @Index(name = "idx_loa_number",      columnList = "loa_number"),
                @Index(name = "idx_loa_employee_id", columnList = "employee_id"),
                @Index(name = "idx_loa_client_id",   columnList = "client_id"),
                @Index(name = "idx_loa_status",      columnList = "status"),
                @Index(name = "idx_loa_is_deleted",  columnList = "is_deleted")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Auto-generated AFTER first insert using the PK id as global counter.
     * Format: LOA-{clientId:02d}-{empId}-{id:04d}
     * e.g.   LOA-01-2026/001-0001
     * Left NULL initially; updated in same transaction immediately after save.
     */
    @Column(name = "loa_number", unique = true, length = 50)
    private String loaNumber;

    /** FK reference — no JPA join to keep cross-module boundary clean */
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    /** Snapshot of employee full name at LOA creation time */
    @Column(name = "employee_name", nullable = false, length = 150)
    private String employeeName;

    /** Snapshot of employee email — used for mail dispatch */
    @Column(name = "employee_email", nullable = false, length = 100)
    private String employeeEmail;

    /** FK reference — no JPA join to keep cross-module boundary clean */
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    /** Snapshot of client name at LOA creation time */
    @Column(name = "client_name", nullable = false, length = 255)
    private String clientName;

    @Column(name = "valid_upto", nullable = false)
    private LocalDate validUpto;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private LoaStatus status = LoaStatus.DRAFT;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = false;
}
