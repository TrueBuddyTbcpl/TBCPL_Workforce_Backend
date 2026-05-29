package com.tbcpl.workforce.hr.performance.entity;

import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalCycleType;
import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_appraisal_cycles",
        indexes = {
                @Index(name = "idx_appr_cycle_type",      columnList = "cycle_type"),
                @Index(name = "idx_appr_cycle_status",    columnList = "status"),
                @Index(name = "idx_appr_cycle_is_active", columnList = "is_active"),
                @Index(name = "idx_appr_cycle_year",      columnList = "appraisal_year")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrAppraisalCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. "Annual Appraisal 2026", "Q1 2026 Review"
    @Column(name = "cycle_name", nullable = false, length = 150)
    private String cycleName;

    @Enumerated(EnumType.STRING)
    @Column(name = "cycle_type", nullable = false, length = 20)
    private AppraisalCycleType cycleType;

    @Column(name = "appraisal_year", nullable = false)
    private Integer appraisalYear;

    // Review period
    @Column(name = "period_start_date", nullable = false)
    private LocalDate periodStartDate;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    // Self-review window
    @Column(name = "self_review_start_date")
    private LocalDate selfReviewStartDate;

    @Column(name = "self_review_end_date")
    private LocalDate selfReviewEndDate;

    // Manager review window
    @Column(name = "manager_review_end_date")
    private LocalDate managerReviewEndDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private AppraisalStatus status = AppraisalStatus.DRAFT;

    @Column(name = "description", length = 500)
    private String description;

    // Which departments this cycle applies to (CSV or ALL)
    @Column(name = "applicable_departments", length = 255)
    @Builder.Default
    private String applicableDepartments = "ALL";

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