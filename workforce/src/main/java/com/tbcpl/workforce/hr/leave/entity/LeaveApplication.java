package com.tbcpl.workforce.hr.leave.entity;

import com.tbcpl.workforce.hr.attendance.entity.LeaveType;
import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_leave_applications",
        indexes = {
                @Index(name = "idx_leave_app_emp_id",    columnList = "emp_id"),
                @Index(name = "idx_leave_app_status",    columnList = "status"),
                @Index(name = "idx_leave_app_from_date", columnList = "from_date"),
                @Index(name = "idx_leave_app_to_date",   columnList = "to_date"),
                @Index(name = "idx_leave_app_is_active", columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Employee applying for leave — cross-dept String ref
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    // Leave type — within HR module, JPA join allowed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    // Actual number of leave days (excluding holidays/weekends if needed)
    @Column(name = "number_of_days", nullable = false)
    private Double numberOfDays;

    @Column(name = "is_half_day", nullable = false)
    @Builder.Default
    private Boolean isHalfDay = false;

    @Column(name = "reason", nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private LeaveStatus status = LeaveStatus.PENDING;

    // Reviewer — the HR/Manager who approved or rejected
    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewer_remarks", length = 500)
    private String reviewerRemarks;

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