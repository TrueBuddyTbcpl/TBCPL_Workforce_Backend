package com.tbcpl.workforce.operation.profile.entity;

import com.tbcpl.workforce.operation.profile.enums.StepStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_step_status", indexes = {
        @Index(name = "idx_step_status_profile_id", columnList = "profile_id"),
        @Index(name = "idx_step_number", columnList = "step_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileStepStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private OpProfile profile;

    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;

    @Column(name = "step_name", nullable = false, length = 100)
    private String stepName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StepStatus status = StepStatus.NOT_FILLED;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
