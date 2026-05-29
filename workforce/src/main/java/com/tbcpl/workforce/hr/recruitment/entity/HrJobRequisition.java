package com.tbcpl.workforce.hr.recruitment.entity;

import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_job_requisitions",
        indexes = {
                @Index(name = "idx_job_req_dept",       columnList = "department"),
                @Index(name = "idx_job_req_status",     columnList = "status"),
                @Index(name = "idx_job_req_is_active",  columnList = "is_active"),
                @Index(name = "idx_job_req_code",       columnList = "requisition_code", unique = true)
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrJobRequisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auto-generated unique code e.g. JR-2026-001
    @Column(name = "requisition_code", nullable = false, unique = true, length = 30)
    private String requisitionCode;

    @Column(name = "job_title", nullable = false, length = 150)
    private String jobTitle;

    // Department requesting the hire — String ref, no cross-dept join
    @Column(name = "department", nullable = false, length = 50)
    private String department;

    @Column(name = "designation", length = 100)
    private String designation;

    @Column(name = "number_of_positions", nullable = false)
    private Integer numberOfPositions;

    @Column(name = "filled_positions", nullable = false)
    @Builder.Default
    private Integer filledPositions = 0;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "required_experience_years")
    private Integer requiredExperienceYears;

    @Column(name = "required_skills", length = 500)
    private String requiredSkills;

    @Column(name = "min_salary_budget")
    private Double minSalaryBudget;

    @Column(name = "max_salary_budget")
    private Double maxSalaryBudget;

    @Column(name = "target_joining_date")
    private LocalDate targetJoiningDate;

    // Who raised this requisition — String ref
    @Column(name = "raised_by", length = 100)
    private String raisedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private RecruitmentStatus status = RecruitmentStatus.OPEN;

    @Column(name = "closure_remarks", length = 255)
    private String closureRemarks;

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