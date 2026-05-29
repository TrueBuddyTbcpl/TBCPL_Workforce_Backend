package com.tbcpl.workforce.hr.grievance.entity;

import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryActionType;
import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_disciplinary_actions",
        indexes = {
                @Index(name = "idx_disc_emp_id",       columnList = "emp_id"),
                @Index(name = "idx_disc_action_type",  columnList = "action_type"),
                @Index(name = "idx_disc_status",       columnList = "status"),
                @Index(name = "idx_disc_case_ref",     columnList = "case_reference", unique = true),
                @Index(name = "idx_disc_is_active",    columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrDisciplinaryAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Auto-generated e.g. DISC-2026-001
    @Column(name = "case_reference", nullable = false, unique = true, length = 30)
    private String caseReference;

    // Employee against whom action is initiated
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 30)
    private DisciplinaryActionType actionType;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "incident_description", columnDefinition = "TEXT", nullable = false)
    private String incidentDescription;

    @Column(name = "incident_date", nullable = false)
    private LocalDate incidentDate;

    // Link to grievance if this action stems from one
    @Column(name = "related_grievance_id")
    private Long relatedGrievanceId;

    // Notice/letter details
    @Column(name = "notice_issued_date")
    private LocalDate noticeIssuedDate;

    @Column(name = "notice_document_url", length = 500)
    private String noticeDocumentUrl;

    // Employee's response / explanation
    @Column(name = "employee_response", columnDefinition = "TEXT")
    private String employeeResponse;

    @Column(name = "employee_response_date")
    private LocalDate employeeResponseDate;

    // Final decision
    @Column(name = "final_decision", columnDefinition = "TEXT")
    private String finalDecision;

    @Column(name = "action_effective_date")
    private LocalDate actionEffectiveDate;

    // For SUSPENSION — end date
    @Column(name = "action_end_date")
    private LocalDate actionEndDate;

    // For SALARY_DEDUCTION — amount
    @Column(name = "deduction_amount")
    private Double deductionAmount;

    // Initiated by HR staff
    @Column(name = "initiated_by", length = 100)
    private String initiatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private DisciplinaryStatus status = DisciplinaryStatus.INITIATED;

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