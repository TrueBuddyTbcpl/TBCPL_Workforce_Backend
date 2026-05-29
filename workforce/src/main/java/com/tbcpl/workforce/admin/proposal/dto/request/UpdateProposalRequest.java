package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.ServiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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
     * Optional — update service type.
     */
    private ServiceType serviceType;

    /**
     * Optional — update product name, max 200 characters.
     */
    @Size(max = 200, message = "productName must not exceed 200 characters")
    private String productName;

    /**
     * When provided, this is a FULL REPLACE of all sections.
     * For partial section updates use the dedicated section-level endpoints.
     */
    @Valid
    private List<ProposalSectionRequest> sections;
}