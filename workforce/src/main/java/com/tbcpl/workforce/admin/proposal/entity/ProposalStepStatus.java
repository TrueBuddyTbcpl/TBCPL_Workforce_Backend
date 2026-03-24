package com.tbcpl.workforce.admin.proposal.entity;

import com.tbcpl.workforce.admin.proposal.entity.enums.StepName;
import com.tbcpl.workforce.admin.proposal.entity.enums.StepStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "proposal_step_status",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_step_proposal_step_name",
                columnNames = {"proposal_id", "step_name"}
        ),
        indexes = @Index(name = "idx_step_proposal_id", columnList = "proposal_id")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProposalStepStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_name", nullable = false, length = 50)
    private StepName stepName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StepStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;
}
