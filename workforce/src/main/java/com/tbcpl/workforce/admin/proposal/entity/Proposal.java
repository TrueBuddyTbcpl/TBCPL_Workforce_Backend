package com.tbcpl.workforce.admin.proposal.entity;

import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "proposals",
        indexes = {
                @Index(name = "idx_proposal_code",      columnList = "proposal_code"),
                @Index(name = "idx_proposal_client_id", columnList = "client_id"),
                @Index(name = "idx_proposal_status",    columnList = "status"),
                @Index(name = "idx_proposal_deleted",   columnList = "deleted")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "proposal_code", nullable = false, unique = true, length = 30)
    private String proposalCode;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private ProposalStatus status;

    /**
     * Ordered list of dynamic sections.
     * cascade = ALL → saving proposal saves/updates sections.
     * orphanRemoval = true → removing from list deletes the row.
     * Ordered by display_order to avoid extra sorting in memory.
     */
    @Builder.Default
    @OneToMany(
            mappedBy      = "proposal",
            cascade       = CascadeType.ALL,
            orphanRemoval = true,
            fetch         = FetchType.LAZY
    )
    @OrderBy("displayOrder ASC")
    private List<ProposalSection> sections = new ArrayList<>();

    @Column(name = "remarks", columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_by", nullable = false, length = 60)
    private String createdBy;

    @Column(name = "updated_by", length = 60)
    private String updatedBy;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_by", length = 60)
    private String deletedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}