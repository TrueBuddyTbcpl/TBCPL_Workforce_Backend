package com.tbcpl.workforce.admin.proposal.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProposalMethodologyRequest {
    private List<String> desktopDueDiligencePoints;
    private List<String> marketGroundIntelligencePoints;
    private List<String> productVerificationPoints;
    private List<String> testPurchasePoints;
}
