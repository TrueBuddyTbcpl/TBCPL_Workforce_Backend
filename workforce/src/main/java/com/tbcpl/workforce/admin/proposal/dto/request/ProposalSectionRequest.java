package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.SectionContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSectionRequest {

    /**
     * Machine key — e.g. "background", "scope_of_work"
     * Frontend uses this to know how to render the section.
     */
    @NotBlank(message = "sectionKey is required")
    private String sectionKey;

    /**
     * Display title user sees — e.g. "Background", "Scope of Work"
     */
    @NotBlank(message = "sectionTitle is required")
    private String sectionTitle;

    /**
     * Determines how content is interpreted.
     */
    @NotNull(message = "contentType is required")
    private SectionContentType contentType;

    /**
     * Raw content string.
     * - TEXT    → plain text
     * - LIST    → JSON array string  e.g. ["Item 1","Item 2"]
     * - FEE     → JSON array string  e.g. [{"label":"Due Diligence Fee","amount":130000}]
     * - TABLE   → JSON array string  e.g. [{"key":"GST","value":"18%"}]
     * - CUSTOM  → any valid JSON string
     */
    private String content;

    /**
     * Optional. If null, section is appended at the end.
     */
    private Integer displayOrder;

    /**
     * Optional. Defaults to true.
     */
    @Builder.Default
    private boolean visible = true;
}