package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.TextMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProposalConclusionRequest {

    @NotNull(message = "Mode is required")
    private TextMode paragraphMode;

    private String paragraphText;
}
