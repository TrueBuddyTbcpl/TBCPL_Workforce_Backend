package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep3Request {

    private IntelligenceNature intelNature;
    private SuspectedActivity suspectedActivity;
    private ProductSegment productSegment;
    private SupplyChainStage supplyChainStage;
    private YesNo repeatIntelligence;
    private YesNo multiBrandRisk;
}
