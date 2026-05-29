package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.SectionContentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSectionRequest {

    @NotBlank(message = "sectionKey is required")
    private String sectionKey;

    @NotBlank(message = "sectionTitle is required")
    private String sectionTitle;

    @NotNull(message = "contentType is required")
    private SectionContentType contentType;

    /**
     * Raw content string.
     * - TEXT  -> plain text
     * - LIST  -> JSON array string
     * - FEE   -> JSON array string
     * - TABLE -> JSON string
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

    /**
     * Optional subsections — supports TEXT, LIST, TABLE only.
     */
    @Valid
    private List<ProposalSubSectionRequest> subSections;
}