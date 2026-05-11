package com.tbcpl.workforce.admin.proposal.entity;

import com.tbcpl.workforce.admin.proposal.entity.enums.SectionContentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "proposal_sections",
        indexes = {
                @Index(name = "idx_psection_proposal_id",   columnList = "proposal_id"),
                @Index(name = "idx_psection_content_type",  columnList = "content_type"),
                @Index(name = "idx_psection_display_order", columnList = "display_order")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proposal_id", nullable = false)
    private Proposal proposal;

    /**
     * Machine-readable key — e.g. "background", "scope_of_work", "professional_fee"
     * Used by frontend to identify section type for rendering.
     */
    @Column(name = "section_key", nullable = false, length = 80)
    private String sectionKey;

    /**
     * Human-readable display title — e.g. "Background", "Scope of Work"
     * User can rename this freely.
     */
    @Column(name = "section_title", nullable = false, length = 150)
    private String sectionTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private SectionContentType contentType;

    /**
     * Stores the actual content.
     * - TEXT    → plain string
     * - LIST    → JSON array of strings
     * - FEE     → JSON array of { label, amount, note }
     * - TABLE   → JSON array of { key, value }
     * - CUSTOM  → any valid JSON
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * Controls rendering order. Starts at 1.
     * Frontend reorder → PATCH /sections/reorder updates this.
     */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    /**
     * Soft visibility toggle — hide a section without deleting it.
     */
    @Builder.Default
    @Column(name = "is_visible", nullable = false)
    private boolean visible = true;

    @Column(name = "created_by", nullable = false, length = 60)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}