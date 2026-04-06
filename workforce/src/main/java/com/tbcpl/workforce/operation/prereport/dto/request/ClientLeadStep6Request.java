package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLeadStep6Request {

    private CompletenessLevel qaCompleteness;
    private AccuracyLevel qaAccuracy;
    private YesNoUnknown qaIndependentInvestigation;
    private YesNoUnknown qaPriorConfrontation;
    private RiskLevel qaContaminationRisk;
    private List<CustomRiskEntry> riskCustomData;
}
