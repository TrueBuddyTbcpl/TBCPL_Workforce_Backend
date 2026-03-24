package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.entity.enums.TextMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ProposalConfidentialityRequest {

    @NotNull(message = "Mode is required")
    private TextMode paragraphMode;

    private String paragraphText;
    private List<String> customPoints;
}
