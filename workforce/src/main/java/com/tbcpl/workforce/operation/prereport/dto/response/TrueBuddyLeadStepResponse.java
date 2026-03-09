package com.tbcpl.workforce.operation.prereport.dto.response;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStepResponse {

    private Long id;
    private Long prereportId;

    // Step 1
    private LocalDate dateInternalLeadGeneration;
    private ProductCategory productCategory;
    private InfringementType infringementType;
    private String broadGeography;
    private String clientSpocName;
    private String clientSpocDesignation;
    private NatureOfEntity natureOfEntity;

    // Step 2
    private Boolean scopeIprSupplier;
    private Boolean scopeIprManufacturer;
    private Boolean scopeIprStockist;
    private Boolean scopeMarketVerification;
    private Boolean scopeEtp;
    private Boolean scopeEnforcement;

    // Step 3
    private IntelligenceNature intelNature;
    private SuspectedActivity suspectedActivity;
    private ProductSegment productSegment;
    private SupplyChainStage supplyChainStage;
    private YesNo repeatIntelligence;
    private YesNo multiBrandRisk;

    // Step 4
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

    // Step 5
    private OperationScale obsOperationScale;
    private LikelihoodLevel obsCounterfeitLikelihood;
    private BrandExposure obsBrandExposure;
    private RiskLevel obsEnforcementSensitivity;
    private RiskLevel obsLeakageRisk;

    // Step 6
    private SourceReliability riskSourceReliability;
    private RiskLevel riskClientConflict;
    private YesNo riskImmediateAction;
    private YesNo riskControlledValidation;
    private RiskLevel riskPrematureDisclosure;

    // Step 7
    private TrueBuddyLeadAssessment assessmentOverall;
    private String assessmentRationale;

    // Step 8
    private Boolean recCovertValidation;
    private Boolean recEtp;
    private Boolean recMarketReconnaissance;
    private Boolean recEnforcementDeferred;
    private Boolean recContinuedMonitoring;
    private Boolean recClientSegregation;

    // Step 9
    private String confidentialityNote;

    // Step 10
    private String remarks;

    // Step 11
    private String customDisclaimer;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
