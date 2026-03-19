package com.tbcpl.workforce.operation.prereport.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "prereport_cutom_opt_clientlead",
        indexes = {
                @Index(name = "idx_pcocl_step",    columnList = "step_number"),
                @Index(name = "idx_pcocl_deleted", columnList = "is_deleted")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrereportCustomOptClientLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "option_name", nullable = false, length = 255)
    private String optionName;

    @Column(name = "option_description", columnDefinition = "TEXT")
    private String optionDescription;

    @Column(name = "lead_type", nullable = false, length = 30)
    private String leadType;   // "CLIENT_LEAD" or "TRUEBUDDY_LEAD"


    @Column(name = "created_by", length = 100)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean deleted = false;
}
