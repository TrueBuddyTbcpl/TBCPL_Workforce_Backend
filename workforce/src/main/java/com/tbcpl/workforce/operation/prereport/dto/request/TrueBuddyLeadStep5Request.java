package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.BrandExposure;
import com.tbcpl.workforce.operation.prereport.entity.enums.LikelihoodLevel;
import com.tbcpl.workforce.operation.prereport.entity.enums.OperationScale;
import com.tbcpl.workforce.operation.prereport.entity.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep5Request {

    private OperationScale obsOperationScale;
    private LikelihoodLevel obsCounterfeitLikelihood;
    private BrandExposure obsBrandExposure;
    private RiskLevel obsEnforcementSensitivity;
    private RiskLevel obsLeakageRisk;
}
