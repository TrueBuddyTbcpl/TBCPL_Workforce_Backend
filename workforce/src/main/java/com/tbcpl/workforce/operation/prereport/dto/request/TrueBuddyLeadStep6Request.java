package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.RiskLevel;
import com.tbcpl.workforce.operation.prereport.entity.enums.SourceReliability;
import com.tbcpl.workforce.operation.prereport.entity.enums.YesNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep6Request {

    private SourceReliability riskSourceReliability;
    private RiskLevel riskClientConflict;
    private YesNo riskImmediateAction;
    private YesNo riskControlledValidation;
    private RiskLevel riskPrematureDisclosure;
}
