package com.tbcpl.workforce.hr.attendance.entity;

import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_leave_types",
        indexes = {
                @Index(name = "idx_leave_type_name",     columnList = "leave_type_name", unique = true),
                @Index(name = "idx_leave_type_category",  columnList = "category"),
                @Index(name = "idx_leave_type_is_active", columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "leave_type_name", nullable = false, unique = true, length = 100)
    private String leaveTypeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private LeaveCategory category;

    @Column(name = "description", length = 255)
    private String description;

    // Max leaves allowed per year for this type
    @Column(name = "max_days_per_year", nullable = false)
    private Integer maxDaysPerYear;

    // Can leaves be carried forward to next year?
    @Column(name = "is_carry_forward_allowed", nullable = false)
    @Builder.Default
    private Boolean isCarryForwardAllowed = false;

    // Max days that can be carried forward
    @Column(name = "max_carry_forward_days")
    private Integer maxCarryForwardDays;

    // Is half-day leave allowed for this type?
    @Column(name = "is_half_day_allowed", nullable = false)
    @Builder.Default
    private Boolean isHalfDayAllowed = true;

    // Should this leave type be deducted from salary if exceeded?
    @Column(name = "is_paid", nullable = false)
    @Builder.Default
    private Boolean isPaid = true;

    // Minimum days notice required before applying
    @Column(name = "min_notice_days")
    @Builder.Default
    private Integer minNoticeDays = 0;

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