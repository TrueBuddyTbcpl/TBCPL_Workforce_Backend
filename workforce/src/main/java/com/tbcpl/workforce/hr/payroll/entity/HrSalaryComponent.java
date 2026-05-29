package com.tbcpl.workforce.hr.payroll.entity;

import com.tbcpl.workforce.hr.payroll.entity.enums.ComponentCalculationType;
import com.tbcpl.workforce.hr.payroll.entity.enums.SalaryComponentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_salary_components",
        indexes = {
                @Index(name = "idx_sal_comp_structure_id", columnList = "salary_structure_id"),
                @Index(name = "idx_sal_comp_type",         columnList = "component_type"),
                @Index(name = "idx_sal_comp_is_active",    columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrSalaryComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salary_structure_id", nullable = false)
    private HrSalaryStructure salaryStructure;

    // e.g. "Basic", "HRA", "PF Employee", "Professional Tax"
    @Column(name = "component_name", nullable = false, length = 100)
    private String componentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "component_type", nullable = false, length = 20)
    private SalaryComponentType componentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "calculation_type", nullable = false, length = 30)
    private ComponentCalculationType calculationType;

    // Used when calculation type is PERCENTAGE_*
    @Column(name = "percentage_value")
    private Double percentageValue;

    // Used when calculation type is FLAT_AMOUNT or STATUTORY
    @Column(name = "flat_amount")
    private Double flatAmount;

    // Monthly computed amount (stored after calculation)
    @Column(name = "monthly_amount", nullable = false)
    private Double monthlyAmount;

    // Annual amount
    @Column(name = "annual_amount", nullable = false)
    private Double annualAmount;

    // Is this a statutory/mandatory component?
    @Column(name = "is_statutory", nullable = false)
    @Builder.Default
    private Boolean isStatutory = false;

    // Display order in payslip
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

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