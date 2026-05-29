package com.tbcpl.workforce.hr.leave.entity;

import com.tbcpl.workforce.hr.attendance.entity.LeaveType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_leave_balances",
        indexes = {
                @Index(name = "idx_leave_balance_emp_id",    columnList = "emp_id"),
                @Index(name = "idx_leave_balance_year",      columnList = "balance_year"),
                @Index(name = "idx_leave_balance_is_active", columnList = "is_active")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name        = "uq_leave_balance_emp_type_year",
                        columnNames = {"emp_id", "leave_type_id", "balance_year"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-dept reference — no JPA join
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    // Leave type reference — within HR module, JPA join allowed
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "balance_year", nullable = false)
    private Integer balanceYear;

    // Total allocated leaves for this type in this year
    @Column(name = "total_allocated", nullable = false)
    private Double totalAllocated;

    // Leaves taken (approved)
    @Column(name = "total_used", nullable = false)
    @Builder.Default
    private Double totalUsed = 0.0;

    // Leaves pending approval
    @Column(name = "total_pending", nullable = false)
    @Builder.Default
    private Double totalPending = 0.0;

    // Leaves carried forward from previous year
    @Column(name = "carried_forward", nullable = false)
    @Builder.Default
    private Double carriedForward = 0.0;

    // Computed: totalAllocated + carriedForward - totalUsed
    @Column(name = "available_balance", nullable = false)
    @Builder.Default
    private Double availableBalance = 0.0;

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

    // ── Helper ────────────────────────────────────────────────────────────────
    public void recalculateAvailableBalance() {
        this.availableBalance =
                (this.totalAllocated + this.carriedForward) - this.totalUsed;
    }
}