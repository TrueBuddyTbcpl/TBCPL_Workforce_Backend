package com.tbcpl.workforce.operation.cases.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CaseResponse {

    // ── Identity ───────────────────────────────────────────────────────
    private Long id;
    private String caseNumber;
    private String prereportReportId;
    private String leadType;

    // ── Basic Info ─────────────────────────────────────────────────────
    private String caseTitle;
    private String priority;
    private String status;
    private String caseType;
    private LocalDate dateOpened;
    private LocalDate dateClosed;

    // ── Client Details ─────────────────────────────────────────────────
    private Long clientId;
    private String clientName;
    private String clientProduct;
    private String clientSpocName;
    private String clientSpocContact;
    private String clientSpocDesignation;
    private String clientEmail;

    // ── Subject / Entity ───────────────────────────────────────────────
    private String entityName;
    private String suspectName;
    private String contactNumbers;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String productDetails;

    // ── Evidence Flags ─────────────────────────────────────────────────
    private String photosProvided;
    private String videoProvided;
    private String invoiceAvailable;

    // ── Source ─────────────────────────────────────────────────────────
    private String sourceNarrative;

    // ── Scope ──────────────────────────────────────────────────────────
    private Boolean scopeDueDiligence;
    private Boolean scopeIprRetailer;
    private Boolean scopeIprSupplier;
    private Boolean scopeIprManufacturer;
    private Boolean scopeOnlinePurchase;
    private Boolean scopeOfflinePurchase;
    private Boolean scopeIprStockist;
    private Boolean scopeMarketVerification;
    private Boolean scopeEtp;
    private Boolean scopeEnforcement;

    // ── TrueBuddy Intelligence ─────────────────────────────────────────
    private String broadGeography;
    private String natureOfEntity;
    private String productCategory;
    private String infringementType;
    private String intelNature;
    private String suspectedActivity;
    private String productSegment;
    private String supplyChainStage;
    private String repeatIntelligence;
    private String multiBrandRisk;

    // ── Verification ───────────────────────────────────────────────────
    private String verificationClientDiscussion;
    private String verificationClientDiscussionNotes;
    private String verificationOsint;
    private String verificationOsintNotes;
    private String verificationMarketplace;
    private String verificationMarketplaceNotes;
    private String verificationPretextCalling;
    private String verificationPretextCallingNotes;
    private String verificationProductReview;
    private String verificationProductReviewNotes;
    private String verificationIntelCorroboration;
    private String verificationIntelCorroborationNotes;
    private String verificationPatternMapping;
    private String verificationPatternMappingNotes;
    private String verificationJurisdiction;
    private String verificationJurisdictionNotes;
    private String verificationRiskAssessment;
    private String verificationRiskAssessmentNotes;

    // ── Observations ───────────────────────────────────────────────────
    private String obsIdentifiableTarget;
    private String obsTraceability;
    private String obsProductVisibility;
    private String obsCounterfeitingIndications;
    private String obsEvidentiarygaps;
    private String obsOperationScale;
    private String obsCounterfeitLikelihood;
    private String obsBrandExposure;
    private String obsEnforcementSensitivity;
    private String obsLeakageRisk;

    // ── QA ─────────────────────────────────────────────────────────────
    private String qaCompleteness;
    private String qaAccuracy;
    private String qaIndependentInvestigation;
    private String qaPriorConfrontation;
    private String qaContaminationRisk;

    // ── Risk ───────────────────────────────────────────────────────────
    private String riskSourceReliability;
    private String riskClientConflict;
    private String riskImmediateAction;
    private String riskControlledValidation;
    private String riskPrematureDisclosure;

    // ── Assessment ─────────────────────────────────────────────────────
    private String assessmentOverall;
    private String assessmentRationale;

    // ── Recommendations ────────────────────────────────────────────────
    private Boolean recMarketSurvey;
    private Boolean recCovertInvestigation;
    private Boolean recTestPurchase;
    private Boolean recEnforcementAction;
    private Boolean recAdditionalInfo;
    private Boolean recClosureHold;
    private Boolean recCovertValidation;
    private Boolean recEtp;
    private Boolean recMarketReconnaissance;
    private Boolean recEnforcementDeferred;
    private Boolean recContinuedMonitoring;
    private Boolean recClientSegregation;

    // ── Closing ────────────────────────────────────────────────────────
    private String confidentialityNote;
    private String remarks;
    private String customDisclaimer;

    // ── Investigation ──────────────────────────────────────────────────
    private List<String> assignedEmployees;
    private LocalDate estimatedCompletionDate;
    private LocalDate actualCompletionDate;

    // ── Relations ──────────────────────────────────────────────────────
    private List<CaseOnlinePresenceResponse> onlinePresences;
    private List<CaseUpdateResponse> updates;

    // ── Audit ──────────────────────────────────────────────────────────
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
