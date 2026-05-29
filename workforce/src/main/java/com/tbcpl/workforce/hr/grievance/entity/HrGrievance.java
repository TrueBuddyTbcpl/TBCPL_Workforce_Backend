package com.tbcpl.workforce.hr.grievance.entity;

import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceCategory;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievancePriority;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "hr_grievances",
        indexes = {
                @Index(name = "idx_grievance_emp_id",    columnList = "emp_id"),
                @Index(name = "idx_grievance_status",    columnList = "status"),
                @Index(name = "idx_grievance_category",  columnList = "category"),
                @Index(name = "idx_grievance_priority",  columnList = "priority"),
                @Index(name = "idx_grievance_ticket",    columnList = "ticket_number", unique = true),
                @Index(name = "idx_grievance_is_active", columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrGrievance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auto-generated ticket e.g. GRV-2026-001
    @Column(name = "ticket_number", nullable = false, unique = true, length = 30)
    private String ticketNumber;

    // Grievance raised by this employee
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private GrievanceCategory category;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    // Supporting document URL
    @Column(name = "attachment_url", length = 500)
    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 20)
    @Builder.Default
    private GrievancePriority priority = GrievancePriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private GrievanceStatus status = GrievanceStatus.SUBMITTED;

    // HR staff assigned to handle this grievance
    @Column(name = "assigned_to", length = 100)
    private String assignedTo;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    // Resolution details
    @Column(name = "resolution_remarks", columnDefinition = "TEXT")
    private String resolutionRemarks;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by", length = 100)
    private String resolvedBy;

    // Is the grievance anonymous?
    @Column(name = "is_anonymous", nullable = false)
    @Builder.Default
    private Boolean isAnonymous = false;

    // Remarks thread for this grievance
    @OneToMany(
            mappedBy      = "grievance",
            cascade       = CascadeType.ALL,
            orphanRemoval = true,
            fetch         = FetchType.LAZY
    )
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<HrGrievanceRemark> remarks = new ArrayList<>();

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