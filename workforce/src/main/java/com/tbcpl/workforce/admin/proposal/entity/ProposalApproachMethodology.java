package com.tbcpl.workforce.admin.proposal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "proposal_approach_methodology",
        indexes = @Index(name = "idx_method_proposal_id", columnList = "proposal_id")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProposalApproachMethodology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false, unique = true)
    private Proposal proposal;

    @Lob
    @Column(name = "desktop_due_diligence_points", columnDefinition = "LONGTEXT")
    private String desktopDueDiligencePoints;

    @Lob
    @Column(name = "market_ground_intelligence_points", columnDefinition = "LONGTEXT")
    private String marketGroundIntelligencePoints;

    @Lob
    @Column(name = "product_verification_points", columnDefinition = "LONGTEXT")
    private String productVerificationPoints;

    @Lob
    @Column(name = "test_purchase_points", columnDefinition = "LONGTEXT")
    private String testPurchasePoints;

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
