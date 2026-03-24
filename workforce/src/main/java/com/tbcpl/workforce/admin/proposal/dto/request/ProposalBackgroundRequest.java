package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.TextMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProposalBackgroundRequest {

    @NotNull(message = "Mode is required")
    private TextMode mode;

    private String backgroundText;
}
