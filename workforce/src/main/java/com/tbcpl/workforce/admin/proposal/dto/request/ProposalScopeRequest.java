package com.tbcpl.workforce.admin.proposal.dto.request;

import com.tbcpl.workforce.admin.proposal.dto.inner.ScopeItemDto;
import lombok.Data;

import java.util.List;

@Data
public class ProposalScopeRequest {
    private List<ScopeItemDto> scopeItems;
}
