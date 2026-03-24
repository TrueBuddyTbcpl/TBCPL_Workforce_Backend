package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.dto.inner.FeeComponentDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProposalFeeResponse {
    private Long id;
    private BigDecimal dueDiligenceFeeAmount;
    private List<FeeComponentDto> feeComponents;
    private List<String> specialConditions;
}
