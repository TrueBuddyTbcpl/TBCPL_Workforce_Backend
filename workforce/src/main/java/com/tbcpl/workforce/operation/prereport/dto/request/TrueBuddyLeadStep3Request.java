package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep3Request {

    private IntelligenceNature intelNature;
    private String intelNatureCustomText;           // ← CHANGED: Long → String
    private SuspectedActivity suspectedActivity;
    private String suspectedActivityCustomText;     // ← CHANGED: Long → String
    private ProductSegment productSegment;
    private String productSegmentCustomText;        // ← CHANGED: Long → String
    private YesNo repeatIntelligence;
    private YesNo multiBrandRisk;
}