package com.tbcpl.workforce.hr.performance.entity;

import com.tbcpl.workforce.hr.performance.entity.enums.KraStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_kra_templates",
        indexes = {
                @Index(name = "idx_kra_tmpl_designation",  columnList = "designation"),
                @Index(name = "idx_kra_tmpl_department",   columnList = "department"),
                @Index(name = "idx_kra_tmpl_status",       columnList = "status"),
                @Index(name = "idx_kra_tmpl_is_active",    columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrKraTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // KRA name e.g. "Code Quality", "Client Satisfaction", "Attendance"
    @Column(name = "kra_name", nullable = false, length = 150)
    private String kraName;

    @Column(name = "kra_description", columnDefinition = "TEXT")
    private String kraDescription;

    // Target designation this KRA applies to
    @Column(name = "designation", length = 100)
    private String designation;

    // Target department (null = applies to all)
    @Column(name = "department", length = 50)
    private String department;

    // Weightage out of 100 for this KRA
    @Column(name = "weightage", nullable = false)
    private Double weightage;

    // Max achievable target value (numeric or descriptive)
    @Column(name = "target_value", length = 100)
    private String targetValue;

    @Column(name = "measurement_unit", length = 50)
    private String measurementUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private KraStatus status = KraStatus.ACTIVE;

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