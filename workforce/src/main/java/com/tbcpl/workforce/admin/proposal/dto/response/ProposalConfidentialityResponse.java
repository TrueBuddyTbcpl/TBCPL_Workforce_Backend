package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.TextMode;
import lombok.Data;

import java.util.List;

@Data
public class ProposalConfidentialityResponse {
    private Long id;
    private TextMode paragraphMode;
    private String paragraphText;
    private List<String> customPoints;
}
