package com.tbcpl.workforce.admin.proposal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "proposal_special_obligations",
        indexes = @Index(name = "idx_oblig_proposal_id", columnList = "proposal_id")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProposalSpecialObligations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false, unique = true)
    private Proposal proposal;

    @Column(name = "client_id")
    private Long clientId;

    @Lob
    @Column(name = "obligation_points_json", columnDefinition = "LONGTEXT")
    private String obligationPointsJson;

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
