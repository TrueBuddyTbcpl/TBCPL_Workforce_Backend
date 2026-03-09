package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.AccuracyLevel;
import com.tbcpl.workforce.operation.prereport.entity.enums.CompletenessLevel;
import com.tbcpl.workforce.operation.prereport.entity.enums.RiskLevel;
import com.tbcpl.workforce.operation.prereport.entity.enums.YesNo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLeadStep6Request {

    private CompletenessLevel qaCompleteness;
    private AccuracyLevel qaAccuracy;
    private YesNo qaIndependentInvestigation;
    private YesNo qaPriorConfrontation;
    private RiskLevel qaContaminationRisk;
}
