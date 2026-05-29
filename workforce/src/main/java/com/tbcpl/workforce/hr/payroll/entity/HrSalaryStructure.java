package com.tbcpl.workforce.hr.payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "hr_salary_structures",
        indexes = {
                @Index(name = "idx_sal_struct_emp_id",    columnList = "emp_id"),
                @Index(name = "idx_sal_struct_effective",  columnList = "effective_from"),
                @Index(name = "idx_sal_struct_is_active",  columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrSalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cross-dept reference — no JPA join
    @Column(name = "emp_id", nullable = false, length = 20)
    private String empId;

    // Annual CTC in rupees
    @Column(name = "annual_ctc", nullable = false)
    private Double annualCtc;

    // Monthly gross (annual CTC / 12)
    @Column(name = "monthly_gross", nullable = false)
    private Double monthlyGross;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    // null means currently active
    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "revision_remarks", length = 255)
    private String revisionRemarks;

    // One structure has many components
    @OneToMany(
            mappedBy    = "salaryStructure",
            cascade     = CascadeType.ALL,
            orphanRemoval = true,
            fetch       = FetchType.LAZY
    )
    @Builder.Default
    private List<HrSalaryComponent> components = new ArrayList<>();

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