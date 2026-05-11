package com.tbcpl.workforce.admin.proposal.dto.request;

import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProposalRequest {

    private Long clientId;

    /**
     * When provided, this is a FULL REPLACE of all sections.
     * To do partial updates (add/edit/delete individual sections),
     * use the dedicated section-level endpoints instead.
     */
    @Valid
    private List<ProposalSectionRequest> sections;
}