package com.tbcpl.workforce.hr.performance.entity;

import com.tbcpl.workforce.hr.performance.entity.enums.RatingScale;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_kra_ratings",
        indexes = {
                @Index(name = "idx_kra_rating_appraisal", columnList = "employee_appraisal_id"),
                @Index(name = "idx_kra_rating_kra_tmpl",  columnList = "kra_template_id"),
                @Index(name = "idx_kra_rating_is_active", columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrKraRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_appraisal_id", nullable = false)
    private HrEmployeeAppraisal employeeAppraisal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kra_template_id", nullable = false)
    private HrKraTemplate kraTemplate;

    // What employee actually achieved
    @Column(name = "achieved_value", length = 100)
    private String achievedValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "self_rating", length = 30)
    private RatingScale selfRating;

    @Column(name = "self_comments", length = 500)
    private String selfComments;

    @Enumerated(EnumType.STRING)
    @Column(name = "manager_rating", length = 30)
    private RatingScale managerRating;

    @Column(name = "manager_comments", length = 500)
    private String managerComments;

    // Weighted score for this KRA: (managerRating numeric * weightage / 100)
    @Column(name = "weighted_score")
    private Double weightedScore;

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