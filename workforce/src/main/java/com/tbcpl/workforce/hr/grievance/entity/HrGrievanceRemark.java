package com.tbcpl.workforce.hr.grievance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_grievance_remarks",
        indexes = {
                @Index(name = "idx_grv_remark_grievance_id", columnList = "grievance_id"),
                @Index(name = "idx_grv_remark_is_active",    columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrGrievanceRemark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grievance_id", nullable = false)
    private HrGrievance grievance;

    // Who added this remark — empId or HR staff id
    @Column(name = "remarked_by", nullable = false, length = 100)
    private String remarkedBy;

    // EMPLOYEE / HR / MANAGER / ADMIN
    @Column(name = "remarked_by_role", length = 30)
    private String remarkedByRole;

    @Column(name = "remark", columnDefinition = "TEXT", nullable = false)
    private String remark;

    // Internal note — not visible to employee
    @Column(name = "is_internal", nullable = false)
    @Builder.Default
    private Boolean isInternal = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}