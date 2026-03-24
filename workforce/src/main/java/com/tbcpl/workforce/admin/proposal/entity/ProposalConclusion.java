package com.tbcpl.workforce.admin.proposal.entity;

import com.tbcpl.workforce.admin.proposal.entity.enums.TextMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "proposal_conclusion",
        indexes = @Index(name = "idx_concl_proposal_id", columnList = "proposal_id")
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProposalConclusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = false, unique = true)
    private Proposal proposal;

    @Enumerated(EnumType.STRING)
    @Column(name = "paragraph_mode", length = 20)
    private TextMode paragraphMode;

    @Lob
    @Column(name = "paragraph_text", columnDefinition = "LONGTEXT")
    private String paragraphText;

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
