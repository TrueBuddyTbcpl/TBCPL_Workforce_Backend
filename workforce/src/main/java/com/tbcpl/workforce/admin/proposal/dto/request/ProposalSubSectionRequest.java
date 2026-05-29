package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.SubSectionContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalSubSectionRequest {

    @NotBlank(message = "subSectionKey is required")
    private String subSectionKey;

    /**
     * Optional field.
     */
    private String subSectionTitle;

    /**
     * Only TEXT, LIST, TABLE allowed for subsections.
     */
    @NotNull(message = "contentType is required")
    private SubSectionContentType contentType;

    /**
     * Raw content string.
     * - TEXT  -> plain text
     * - LIST  -> JSON array string
     * - TABLE -> JSON string
     */
    private String content;

    /**
     * Optional. If null, subsection is appended at end within parent section.
     */
    private Integer displayOrder;

    /**
     * Optional. Defaults to true.
     */
    @Builder.Default
    private boolean visible = true;
}