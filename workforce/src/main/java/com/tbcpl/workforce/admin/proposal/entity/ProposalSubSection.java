package com.tbcpl.workforce.admin.proposal.entity;

import com.tbcpl.workforce.admin.proposal.entity.enums.SubSectionContentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposal_sub_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSubSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private ProposalSection section;

    @Column(name = "sub_section_key", nullable = false, length = 100)
    private String subSectionKey;

    @Column(name = "sub_section_title", nullable = true, length = 255)
    private String subSectionTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private SubSectionContentType contentType;

    @Lob
    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "visible", nullable = false)
    @Builder.Default
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