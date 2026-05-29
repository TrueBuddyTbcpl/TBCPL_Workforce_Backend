package com.tbcpl.workforce.hr.performance.entity;

import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalStatus;
import com.tbcpl.workforce.hr.performance.entity.enums.RatingScale;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "hr_employee_appraisals",
        indexes = {
                @Index(name = "idx_emp_appr_emp_id",      columnList = "emp_id"),
                @Index(name = "idx_emp_appr_cycle_id",    columnList = "appraisal_cycle_id"),
                @Index(name = "idx_emp_appr_status",      columnList = "status"),
                @Index(name = "idx_emp_appr_is_active",   columnList = "is_active")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name  = "uq_emp_appraisal_cycle",
                        columnNames = {"emp_id", "appraisal_cycle_id"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrEmployeeAppraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-dept reference — no JPA join
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appraisal_cycle_id", nullable = false)
    private HrAppraisalCycle appraisalCycle;

    // Manager emp_id — String ref, no cross-dept join
    @Column(name = "manager_emp_id", length = 20)
    private String managerEmpId;

    // ── Self Review ───────────────────────────────────────────────────────────
    @Column(name = "self_review_comments", columnDefinition = "TEXT")
    private String selfReviewComments;

    @Column(name = "self_review_submitted_at")
    private LocalDateTime selfReviewSubmittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "self_rating", length = 30)
    private RatingScale selfRating;

    // ── Manager Review ────────────────────────────────────────────────────────
    @Column(name = "manager_review_comments", columnDefinition = "TEXT")
    private String managerReviewComments;

    @Column(name = "manager_review_submitted_at")
    private LocalDateTime managerReviewSubmittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "manager_rating", length = 30)
    private RatingScale managerRating;

    // ── HR / Final Review ─────────────────────────────────────────────────────
    @Column(name = "hr_review_comments", columnDefinition = "TEXT")
    private String hrReviewComments;

    @Column(name = "hr_review_submitted_at")
    private LocalDateTime hrReviewSubmittedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "final_rating", length = 30)
    private RatingScale finalRating;

    // Weighted average score (computed from KRA ratings)
    @Column(name = "final_score")
    private Double finalScore;

    // Salary increment recommended by HR
    @Column(name = "increment_percentage")
    private Double incrementPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private AppraisalStatus status = AppraisalStatus.SELF_REVIEW_PENDING;

    // KRA ratings for this appraisal
    @OneToMany(
            mappedBy      = "employeeAppraisal",
            cascade       = CascadeType.ALL,
            orphanRemoval = true,
            fetch         = FetchType.LAZY
    )
    @Builder.Default
    private List<HrKraRating> kraRatings = new ArrayList<>();

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