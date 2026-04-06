package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep5Request {

    private OperationScale obsOperationScale;
    private LikelihoodLevel obsCounterfeitLikelihood;
    private BrandExposure obsBrandExposure;
    private String obsBrandExposureCustomText;      // ← CHANGED: Long → String
    private RiskLevel obsEnforcementSensitivity;
    private List<CustomObservationEntry> observationsCustomData;
}