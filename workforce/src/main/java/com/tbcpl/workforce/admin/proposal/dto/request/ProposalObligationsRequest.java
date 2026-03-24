package com.tbcpl.workforce.admin.proposal.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ProposalObligationsRequest {
    private List<String> obligationPoints;
}
