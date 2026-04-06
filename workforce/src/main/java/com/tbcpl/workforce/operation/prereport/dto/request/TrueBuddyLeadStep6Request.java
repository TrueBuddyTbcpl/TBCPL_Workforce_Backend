package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep6Request {

    private SourceReliability riskSourceReliability;
    private RiskLevel riskClientConflict;
    private YesNo riskImmediateAction;
    private YesNo riskControlledValidation;
    private List<CustomRiskEntry> riskCustomData;
}