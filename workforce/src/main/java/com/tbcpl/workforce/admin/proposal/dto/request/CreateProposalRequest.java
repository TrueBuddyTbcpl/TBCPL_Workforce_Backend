package com.tbcpl.workforce.admin.proposal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProposalRequest {

    @NotNull(message = "clientId is required")
    private Long clientId;

    /**
     * Optional initial sections at creation time.
     * If not provided, proposal is created with zero sections (pure DRAFT).
     */
    @Valid
    private List<ProposalSectionRequest> sections;
}