package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.TextMode;
import lombok.Data;

@Data
public class ProposalConclusionResponse {
    private Long id;
    private TextMode paragraphMode;
    private String paragraphText;
}
