package com.tbcpl.workforce.hr.payroll.entity;

import com.tbcpl.workforce.hr.payroll.entity.enums.PayrollInputType;
import com.tbcpl.workforce.hr.payroll.entity.enums.PayrollStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_payroll_inputs",
        indexes = {
                @Index(name = "idx_payroll_input_emp_id",    columnList = "emp_id"),
                @Index(name = "idx_payroll_input_month",     columnList = "payroll_month"),
                @Index(name = "idx_payroll_input_year",      columnList = "payroll_year"),
                @Index(name = "idx_payroll_input_status",    columnList = "status"),
                @Index(name = "idx_payroll_input_is_active", columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrPayrollInput {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-dept reference — no JPA join
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    @Column(name = "payroll_month", nullable = false)
    private Integer payrollMonth;

    @Column(name = "payroll_year", nullable = false)
    private Integer payrollYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false, length = 30)
    private PayrollInputType inputType;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "description", length = 255)
    private String description;

    // How many LWP days (used when inputType = LEAVE_WITHOUT_PAY)
    @Column(name = "lwp_days")
    private Double lwpDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PayrollStatus status = PayrollStatus.DRAFT;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "submitted_by", length = 100)
    private String submittedBy;

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