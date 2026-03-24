package com.tbcpl.workforce.admin.proposal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "proposal_professional_fee",
        indexes = @Index(name = "idx_fee_proposal_id", columnList = "proposal_id")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProposalProfessionalFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false, unique = true)
    private Proposal proposal;

    @Column(name = "due_diligence_fee_amount", precision = 12, scale = 2)
    private BigDecimal dueDiligenceFeeAmount;

    @Lob
    @Column(name = "fee_components_json", columnDefinition = "LONGTEXT")
    private String feeComponentsJson;

    @Lob
    @Column(name = "special_conditions_json", columnDefinition = "LONGTEXT")
    private String specialConditionsJson;

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
