package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProposalStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private ProposalStatus status;

    // Required when status = REQUEST_FOR_CHANGES
    private String remarks;

    // Optional: comma-separated or list of step names that need changes
    private String sectionsNeedingChanges;
}
