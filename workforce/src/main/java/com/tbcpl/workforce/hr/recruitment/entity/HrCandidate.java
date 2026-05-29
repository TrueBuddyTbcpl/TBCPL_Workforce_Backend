package com.tbcpl.workforce.hr.recruitment.entity;

import com.tbcpl.workforce.hr.recruitment.entity.enums.CandidateSource;
import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferStatus;
import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_candidates",
        indexes = {
                @Index(name = "idx_candidate_email",        columnList = "email"),
                @Index(name = "idx_candidate_phone",        columnList = "phone"),
                @Index(name = "idx_candidate_requisition",  columnList = "job_requisition_id"),
                @Index(name = "idx_candidate_status",       columnList = "status"),
                @Index(name = "idx_candidate_offer_status", columnList = "offer_status"),
                @Index(name = "idx_candidate_is_active",    columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which requisition this candidate is applying for
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_requisition_id", nullable = false)
    private HrJobRequisition jobRequisition;

    // Personal Info
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "current_company", length = 150)
    private String currentCompany;

    @Column(name = "current_designation", length = 100)
    private String currentDesignation;

    @Column(name = "total_experience_years")
    private Double totalExperienceYears;

    @Column(name = "current_ctc")
    private Double currentCtc;

    @Column(name = "expected_ctc")
    private Double expectedCtc;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    @Column(name = "resume_url", length = 500)
    private String resumeUrl;

    @Column(name = "linkedin_url", length = 255)
    private String linkedinUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 30)
    private CandidateSource source;

    // Who referred (if REFERRAL)
    @Column(name = "referred_by", length = 100)
    private String referredBy;

    // Current stage in recruitment pipeline
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private RecruitmentStatus status = RecruitmentStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status", nullable = false, length = 20)
    @Builder.Default
    private OfferStatus offerStatus = OfferStatus.NOT_OFFERED;

    @Column(name = "offered_ctc")
    private Double offeredCtc;

    @Column(name = "offer_date")
    private LocalDate offerDate;

    @Column(name = "expected_joining_date")
    private LocalDate expectedJoiningDate;

    // If JOINED — link to the created employee record
    @Column(name = "emp_id", length = 20)
    private String empId;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "hr_remarks", length = 500)
    private String hrRemarks;

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