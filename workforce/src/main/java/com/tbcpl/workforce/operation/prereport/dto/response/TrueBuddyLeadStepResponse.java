package com.tbcpl.workforce.operation.prereport.dto.response;

import com.tbcpl.workforce.operation.prereport.dto.request.*;
import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStepResponse {

    private Long id;
    private Long prereportId;

    // ── Step 1: Case Reference ────────────────────────────────────────────────
    private LocalDate dateInternalLeadGeneration;
    private ProductCategory productCategory;
    private String productCategoryCustomText;       // ← CHANGED: Long → String
    private InfringementType infringementType;
    private String infringementTypeCustomText;      // ← CHANGED: Long → String
    private String broadGeography;
    private java.util.List<ReasonOfSuspicion> reasonOfSuspicion;
    private String reasonOfSuspicionCustomText;     // ← CHANGED: Long → String
    private String expectedSeizure;
    private NatureOfEntity natureOfEntity;
    private String natureOfEntityCustomText;        // ← CHANGED: Long → String

    // ── Step 2: Scope Proposed ────────────────────────────────────────────────
    private Boolean scopeIprSupplier;
    private Boolean scopeIprManufacturer;
    private Boolean scopeIprStockist;
    private Boolean scopeMarketVerification;
    private Boolean scopeEtp;
    private Boolean scopeEnforcement;
    private List<Long> scopeCustomIds;

    // ── Step 3: Lead Description ──────────────────────────────────────────────
    private IntelligenceNature intelNature;
    private String intelNatureCustomText;           // ← CHANGED: Long → String
    private SuspectedActivity suspectedActivity;
    private String suspectedActivityCustomText;     // ← CHANGED: Long → String
    private ProductSegment productSegment;
    private String productSegmentCustomText;        // ← CHANGED: Long → String
    private YesNo repeatIntelligence;
    private YesNo multiBrandRisk;

    // ── Step 4: Verification ──────────────────────────────────────────────────
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
    private List<CustomVerificationEntry> verificationCustomData;

    // ── Step 5: Observations ──────────────────────────────────────────────────
    private OperationScale obsOperationScale;
    private LikelihoodLevel obsCounterfeitLikelihood;
    private BrandExposure obsBrandExposure;
    private String obsBrandExposureCustomText;      // ← CHANGED: Long → String
    private RiskLevel obsEnforcementSensitivity;
    private List<CustomObservationEntry> observationsCustomData;

    // ── Step 6: Risk Assessment ───────────────────────────────────────────────
    private SourceReliability riskSourceReliability;
    private RiskLevel riskClientConflict;
    private YesNo riskImmediateAction;
    private YesNo riskControlledValidation;
    private List<CustomRiskEntry> riskCustomData;

    // ── Step 7: Assessment ────────────────────────────────────────────────────
    private TrueBuddyLeadAssessment assessmentOverall;
    private String assessmentRationale;

    // ── Step 8: Way Forward ───────────────────────────────────────────────────
    private Boolean recCovertValidation;
    private Boolean recEtp;
    private Boolean recMarketReconnaissance;
    private Boolean recEnforcementDeferred;
    private Boolean recContinuedMonitoring;
    private Boolean recClientSegregation;
    private List<Long> recCustomIds;

    // ── Step 9: Remarks ───────────────────────────────────────────────────────
    private String remarks;

    // ── Step 10: Disclaimer ───────────────────────────────────────────────────
    private String customDisclaimer;

    // ── Audit ─────────────────────────────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}