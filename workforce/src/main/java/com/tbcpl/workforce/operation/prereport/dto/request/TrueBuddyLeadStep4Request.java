package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep4Request {

    private VerificationStatus verificationIntelCorroboration;
    private String verificationIntelCorroborationNotes;
    private VerificationStatus verificationOsint;
    private String verificationOsintNotes;
    private VerificationStatus verificationPatternMapping;
    private String verificationPatternMappingNotes;
    private VerificationStatus verificationJurisdiction;
    private String verificationJurisdictionNotes;
    private VerificationStatus verificationRiskAssessment;
    private String verificationRiskAssessmentNotes;
}
