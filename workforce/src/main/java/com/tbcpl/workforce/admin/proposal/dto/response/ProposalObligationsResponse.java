package com.tbcpl.workforce.admin.proposal.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProposalObligationsResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private List<String> obligationPoints;
}
