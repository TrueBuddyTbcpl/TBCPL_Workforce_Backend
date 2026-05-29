package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.ServiceType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
     * Optional service type dropdown.
     */
    private ServiceType serviceType;

    /**
     * Optional product name, max 200 characters.
     */
    @Size(max = 200, message = "productName must not exceed 200 characters")
    private String productName;

    /**
     * Optional initial sections at creation time.
     */
    @Valid
    private List<ProposalSectionRequest> sections;
}