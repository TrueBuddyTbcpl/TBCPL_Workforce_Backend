package com.tbcpl.workforce.operation.prereport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLeadStep8Request {

    private Boolean recMarketSurvey;
    private Boolean recCovertInvestigation;
    private Boolean recTestPurchase;
    private Boolean recEnforcementAction;
    private Boolean recAdditionalInfo;
    private Boolean recClosureHold;
}
