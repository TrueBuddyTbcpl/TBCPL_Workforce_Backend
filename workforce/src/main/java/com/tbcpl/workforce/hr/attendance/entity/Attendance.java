package com.tbcpl.workforce.hr.attendance.entity;

import com.tbcpl.workforce.hr.attendance.entity.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "hr_attendance",
        indexes = {
                @Index(name = "idx_attendance_emp_id",    columnList = "emp_id"),
                @Index(name = "idx_attendance_date",      columnList = "attendance_date"),
                @Index(name = "idx_attendance_status",    columnList = "status"),
                @Index(name = "idx_attendance_is_active", columnList = "is_active")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name        = "uq_attendance_emp_date",
                        columnNames = {"emp_id", "attendance_date"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-dept reference — plain String, no JPA join
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AttendanceStatus status;

    @Column(name = "punch_in_time")
    private LocalTime punchInTime;

    @Column(name = "punch_out_time")
    private LocalTime punchOutTime;

    // Effective working hours (in decimal, e.g. 8.5)
    @Column(name = "working_hours")
    private Double workingHours;

    @Column(name = "is_regularized", nullable = false)
    @Builder.Default
    private Boolean isRegularized = false;

    @Column(name = "regularization_reason", length = 255)
    private String regularizationReason;

    @Column(name = "remarks", length = 255)
    private String remarks;

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