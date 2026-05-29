package com.tbcpl.workforce.hr.recruitment.entity;

import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferLetterStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_offer_letters",
        indexes = {
                @Index(name = "idx_offer_candidate_id",  columnList = "candidate_id"),
                @Index(name = "idx_offer_status",        columnList = "status"),
                @Index(name = "idx_offer_req_id",        columnList = "job_requisition_id"),
                @Index(name = "idx_offer_is_active",     columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrOfferLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private HrCandidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_requisition_id", nullable = false)
    private HrJobRequisition jobRequisition;

    @Column(name = "designation_offered", nullable = false, length = 100)
    private String designationOffered;

    @Column(name = "department_offered", nullable = false, length = 50)
    private String departmentOffered;

    @Column(name = "ctc_offered", nullable = false)
    private Double ctcOffered;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    // Offer expiry date
    @Column(name = "offer_expiry_date")
    private LocalDate offerExpiryDate;

    // Location of posting
    @Column(name = "work_location", length = 100)
    private String workLocation;

    // URL to the generated offer letter PDF
    @Column(name = "document_url", length = 500)
    private String documentUrl;

    @Column(name = "special_conditions", columnDefinition = "TEXT")
    private String specialConditions;

    // Candidate's response when accepting/rejecting
    @Column(name = "candidate_remarks", length = 1000)
    private String candidateRemarks;

    @Column(name = "candidate_responded_at")
    private LocalDateTime candidateRespondedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private OfferLetterStatus status = OfferLetterStatus.DRAFTED;

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