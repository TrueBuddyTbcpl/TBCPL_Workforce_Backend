package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.dto.inner.ScopeItemDto;
import lombok.Data;

import java.util.List;

@Data
public class ProposalScopeResponse {
    private Long id;
    private List<ScopeItemDto> scopeItems;
}
