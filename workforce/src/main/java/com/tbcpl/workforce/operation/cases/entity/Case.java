package com.tbcpl.workforce.operation.cases.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "op_case", indexes = {
        @Index(name = "idx_case_number", columnList = "case_number", unique = true),
        @Index(name = "idx_case_prereport_id", columnList = "prereport_id"),
        @Index(name = "idx_case_status", columnList = "status"),
        @Index(name = "idx_case_client_id", columnList = "client_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Auto-generated ─────────────────────────────────────────────────
    @Column(name = "case_number", nullable = false, unique = true, length = 30)
    private String caseNumber;

    // ── Auto-filled from PreReport (MANDATORY) ─────────────────────────
    @Column(name = "prereport_id", nullable = false)
    private Long prereportId;

    @Column(name = "prereport_report_id", nullable = false, length = 30)
    private String prereportReportId;

    @Column(name = "lead_type", nullable = false, length = 30)
    private String leadType;

    @Column(name = "case_title", nullable = false, length = 255)
    private String caseTitle;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "open";

    @Column(name = "date_opened", nullable = false)
    private LocalDate dateOpened;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "client_name", nullable = false, length = 150)
    private String clientName;

    @Column(name = "client_product", nullable = false, columnDefinition = "TEXT")
    private String clientProduct;

    // ── Manual Input (OPTIONAL) ────────────────────────────────────────
    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "case_type", length = 100)
    private String caseType;

    @Column(name = "assigned_employees", columnDefinition = "TEXT")
    private String assignedEmployees;

    @Column(name = "estimated_completion_date")
    private LocalDate estimatedCompletionDate;

    @Column(name = "client_email", length = 150)
    private String clientEmail;

    // ── Auto-filled from PreReport (OPTIONAL — depends on step completion)
    @Column(name = "date_closed")
    private LocalDate dateClosed;

    @Column(name = "client_spoc_name", length = 150)
    private String clientSpocName;

    @Column(name = "client_spoc_contact", length = 50)
    private String clientSpocContact;

    @Column(name = "client_spoc_designation", length = 100)
    private String clientSpocDesignation;

    @Column(name = "entity_name", length = 200)
    private String entityName;

    @Column(name = "suspect_name", length = 200)
    private String suspectName;

    @Column(name = "contact_numbers", columnDefinition = "TEXT")
    private String contactNumbers;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 10)
    private String pincode;

    @Column(name = "product_details", columnDefinition = "LONGTEXT")
    private String productDetails;

    @Column(name = "photos_provided", length = 10)
    private String photosProvided;

    @Column(name = "video_provided", length = 10)
    private String videoProvided;

    @Column(name = "invoice_available", length = 10)
    private String invoiceAvailable;

    @Column(name = "source_narrative", columnDefinition = "LONGTEXT")
    private String sourceNarrative;

    // ── Scope — Client Lead ────────────────────────────────────────────
    @Column(name = "scope_due_diligence")
    private Boolean scopeDueDiligence;

    @Column(name = "scope_ipr_retailer")
    private Boolean scopeIprRetailer;

    @Column(name = "scope_ipr_supplier")
    private Boolean scopeIprSupplier;

    @Column(name = "scope_ipr_manufacturer")
    private Boolean scopeIprManufacturer;

    @Column(name = "scope_online_purchase")
    private Boolean scopeOnlinePurchase;

    @Column(name = "scope_offline_purchase")
    private Boolean scopeOfflinePurchase;

    // ── Scope — TrueBuddy Lead ─────────────────────────────────────────
    @Column(name = "scope_ipr_stockist")
    private Boolean scopeIprStockist;

    @Column(name = "scope_market_verification")
    private Boolean scopeMarketVerification;

    @Column(name = "scope_etp")
    private Boolean scopeEtp;

    @Column(name = "scope_enforcement")
    private Boolean scopeEnforcement;

    // ── TrueBuddy Intelligence ─────────────────────────────────────────
    @Column(name = "broad_geography", columnDefinition = "TEXT")
    private String broadGeography;

    @Column(name = "nature_of_entity", length = 100)
    private String natureOfEntity;

    @Column(name = "product_category", length = 100)
    private String productCategory;

    @Column(name = "infringement_type", length = 100)
    private String infringementType;

    @Column(name = "intel_nature", length = 100)
    private String intelNature;

    @Column(name = "suspected_activity", length = 100)
    private String suspectedActivity;

    @Column(name = "product_segment", length = 100)
    private String productSegment;

    @Column(name = "supply_chain_stage", length = 100)
    private String supplyChainStage;

    @Column(name = "repeat_intelligence", length = 10)
    private String repeatIntelligence;

    @Column(name = "multi_brand_risk", length = 10)
    private String multiBrandRisk;

    // ── Verification ───────────────────────────────────────────────────
    @Column(name = "verification_client_discussion", length = 30)
    private String verificationClientDiscussion;

    @Column(name = "verification_client_discussion_notes", columnDefinition = "TEXT")
    private String verificationClientDiscussionNotes;

    @Column(name = "verification_osint", length = 30)
    private String verificationOsint;

    @Column(name = "verification_osint_notes", columnDefinition = "TEXT")
    private String verificationOsintNotes;

    @Column(name = "verification_marketplace", length = 30)
    private String verificationMarketplace;

    @Column(name = "verification_marketplace_notes", columnDefinition = "TEXT")
    private String verificationMarketplaceNotes;

    @Column(name = "verification_pretext_calling", length = 30)
    private String verificationPretextCalling;

    @Column(name = "verification_pretext_calling_notes", columnDefinition = "TEXT")
    private String verificationPretextCallingNotes;

    @Column(name = "verification_product_review", length = 30)
    private String verificationProductReview;

    @Column(name = "verification_product_review_notes", columnDefinition = "TEXT")
    private String verificationProductReviewNotes;

    @Column(name = "verification_intel_corroboration", length = 30)
    private String verificationIntelCorroboration;

    @Column(name = "verification_intel_corroboration_notes", columnDefinition = "TEXT")
    private String verificationIntelCorroborationNotes;

    @Column(name = "verification_pattern_mapping", length = 30)
    private String verificationPatternMapping;

    @Column(name = "verification_pattern_mapping_notes", columnDefinition = "TEXT")
    private String verificationPatternMappingNotes;

    @Column(name = "verification_jurisdiction", length = 30)
    private String verificationJurisdiction;

    @Column(name = "verification_jurisdiction_notes", columnDefinition = "TEXT")
    private String verificationJurisdictionNotes;

    @Column(name = "verification_risk_assessment", length = 30)
    private String verificationRiskAssessment;

    @Column(name = "verification_risk_assessment_notes", columnDefinition = "TEXT")
    private String verificationRiskAssessmentNotes;

    // ── Observations — Client Lead ─────────────────────────────────────
    @Column(name = "obs_identifiable_target", columnDefinition = "LONGTEXT")
    private String obsIdentifiableTarget;

    @Column(name = "obs_traceability", columnDefinition = "LONGTEXT")
    private String obsTraceability;

    @Column(name = "obs_product_visibility", columnDefinition = "LONGTEXT")
    private String obsProductVisibility;

    @Column(name = "obs_counterfeiting_indications", columnDefinition = "LONGTEXT")
    private String obsCounterfeitingIndications;

    @Column(name = "obs_evidentiary_gaps", columnDefinition = "LONGTEXT")
    private String obsEvidentiarygaps;

    // ── Observations — TrueBuddy Lead ──────────────────────────────────
    @Column(name = "obs_operation_scale", length = 50)
    private String obsOperationScale;

    @Column(name = "obs_counterfeit_likelihood", length = 50)
    private String obsCounterfeitLikelihood;

    @Column(name = "obs_brand_exposure", length = 50)
    private String obsBrandExposure;

    @Column(name = "obs_enforcement_sensitivity", length = 50)
    private String obsEnforcementSensitivity;

    @Column(name = "obs_leakage_risk", length = 50)
    private String obsLeakageRisk;

    // ── QA — Client Lead ───────────────────────────────────────────────
    @Column(name = "qa_Completeness", length = 50)
    private String qaCompleteness;

    @Column(name = "qa_accuracy", length = 50)
    private String qaAccuracy;

    @Column(name = "qa_independent_investigation", length = 10)
    private String qaIndependentInvestigation;

    @Column(name = "qa_prior_confrontation", length = 10)
    private String qaPriorConfrontation;

    @Column(name = "qa_contamination_risk", length = 30)
    private String qaContaminationRisk;

    // ── Risk — TrueBuddy Lead ──────────────────────────────────────────
    @Column(name = "risk_source_reliability", length = 50)
    private String riskSourceReliability;

    @Column(name = "risk_client_conflict", length = 30)
    private String riskClientConflict;

    @Column(name = "risk_immediate_action", length = 10)
    private String riskImmediateAction;

    @Column(name = "risk_controlled_validation", length = 10)
    private String riskControlledValidation;

    @Column(name = "risk_premature_disclosure", length = 30)
    private String riskPrematureDisclosure;

    // ── Assessment ─────────────────────────────────────────────────────
    @Column(name = "assessment_overall", length = 100)
    private String assessmentOverall;

    @Column(name = "assessment_rationale", columnDefinition = "LONGTEXT")
    private String assessmentRationale;

    // ── Recommendations — Client Lead ──────────────────────────────────
    @Column(name = "rec_market_survey")
    private Boolean recMarketSurvey;

    @Column(name = "rec_covert_investigation")
    private Boolean recCovertInvestigation;

    @Column(name = "rec_test_purchase")
    private Boolean recTestPurchase;

    @Column(name = "rec_enforcement_action")
    private Boolean recEnforcementAction;

    @Column(name = "rec_additional_info")
    private Boolean recAdditionalInfo;

    @Column(name = "rec_closure_hold")
    private Boolean recClosureHold;

    // ── Recommendations — TrueBuddy Lead ───────────────────────────────
    @Column(name = "rec_covert_validation")
    private Boolean recCovertValidation;

    @Column(name = "rec_etp")
    private Boolean recEtp;

    @Column(name = "rec_market_reconnaissance")
    private Boolean recMarketReconnaissance;

    @Column(name = "rec_enforcement_deferred")
    private Boolean recEnforcementDeferred;

    @Column(name = "rec_continued_monitoring")
    private Boolean recContinuedMonitoring;

    @Column(name = "rec_client_segregation")
    private Boolean recClientSegregation;

    // ── TrueBuddy Confidentiality ──────────────────────────────────────
    @Column(name = "confidentiality_note", columnDefinition = "LONGTEXT")
    private String confidentialityNote;

    // ── Common Closing ─────────────────────────────────────────────────
    @Column(name = "remarks", columnDefinition = "LONGTEXT")
    private String remarks;

    @Column(name = "custom_disclaimer", columnDefinition = "LONGTEXT")
    private String customDisclaimer;

    // ── Investigation ──────────────────────────────────────────────────
    @Column(name = "actual_completion_date")
    private LocalDate actualCompletionDate;

    // ── Relations ──────────────────────────────────────────────────────
    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CaseOnlinePresence> onlinePresences = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CaseDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CaseUpdate> updates = new ArrayList<>();

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CaseLinkedProfile> linkedProfiles = new ArrayList<>();


    // ── Audit ──────────────────────────────────────────────────────────
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        dateOpened = LocalDate.now();
        status = (status == null) ? "open" : status;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
