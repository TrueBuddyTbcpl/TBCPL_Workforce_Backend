package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalServiceType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProposalRequest {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    private String clientCompanyType;
    private String suspectEntityName;
    private String suspectEntityType;
    private String projectTitle;

    @NotNull(message = "Proposal date is required")
    private LocalDate proposalDate;

    private String targetProducts;

    @NotNull(message = "Service type is required")
    private ProposalServiceType serviceType;
}
