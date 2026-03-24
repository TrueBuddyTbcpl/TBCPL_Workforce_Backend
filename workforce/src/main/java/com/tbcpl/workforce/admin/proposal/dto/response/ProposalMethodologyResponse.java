package com.tbcpl.workforce.admin.proposal.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProposalMethodologyResponse {
    private Long id;
    private List<String> desktopDueDiligencePoints;
    private List<String> marketGroundIntelligencePoints;
    private List<String> productVerificationPoints;
    private List<String> testPurchasePoints;
}
