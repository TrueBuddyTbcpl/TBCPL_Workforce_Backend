package com.tbcpl.workforce.operation.prereport.entity;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prereport_truebuddy_lead")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReportTrueBuddyLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prereport_id", unique = true, nullable = false)
    private Long prereportId;

    // ── Step 1: Case Reference ────────────────────────────────────────────────

    @Column(name = "date_internal_lead_generation")
    private LocalDate dateInternalLeadGeneration;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category", length = 100)
    private ProductCategory productCategory;

    @Column(name = "product_category_custom_text", length = 255)    // ← CHANGED: Long → String
    private String productCategoryCustomText;

    @Enumerated(EnumType.STRING)
    @Column(name = "infringement_type", length = 100)
    private InfringementType infringementType;

    @Column(name = "infringement_type_custom_text", length = 255)   // ← CHANGED: Long → String
    private String infringementTypeCustomText;

    @Column(name = "broad_geography", columnDefinition = "TEXT")
    private String broadGeography;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reason_of_suspicion", columnDefinition = "LONGTEXT")
    private List<ReasonOfSuspicion> reasonOfSuspicion;

    @Column(name = "reason_of_suspicion_custom_text", length = 255) // ← CHANGED: Long → String
    private String reasonOfSuspicionCustomText;

    @Column(name = "expected_seizure", columnDefinition = "LONGTEXT")
    private String expectedSeizure;

    @Enumerated(EnumType.STRING)
    @Column(name = "nature_of_entity", length = 100)
    private NatureOfEntity natureOfEntity;

    @Column(name = "nature_of_entity_custom_text", length = 255)    // ← CHANGED: Long → String
    private String natureOfEntityCustomText;

    // ── Step 2: Scope Proposed ────────────────────────────────────────────────

    @Column(name = "scope_ipr_supplier", length = 100)
    @Builder.Default
    private Boolean scopeIprSupplier = false;

    @Column(name = "scope_ipr_manufacturer", length = 100)
    @Builder.Default
    private Boolean scopeIprManufacturer = false;

    @Column(name = "scope_ipr_stockist", length = 100)
    @Builder.Default
    private Boolean scopeIprStockist = false;

    @Column(name = "scope_market_verification", length = 100)
    @Builder.Default
    private Boolean scopeMarketVerification = false;

    @Column(name = "scope_etp", length = 100)
    @Builder.Default
    private Boolean scopeEtp = false;

    @Column(name = "scope_enforcement", length = 100)
    @Builder.Default
    private Boolean scopeEnforcement = false;

    // ── Step 3: Lead Description ──────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "intel_nature", length = 255)          // ← KEY FIX
    private IntelligenceNature intelNature;

    @Column(name = "intel_nature_custom_text", length = 255)        // ← CHANGED: Long → String
    private String intelNatureCustomText;

    @Enumerated(EnumType.STRING)
    @Column(name = "suspected_activity", length = 100)
    private SuspectedActivity suspectedActivity;

    @Column(name = "suspected_activity_custom_text", length = 255)  // ← CHANGED: Long → String
    private String suspectedActivityCustomText;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_segment", length = 100)
    private ProductSegment productSegment;

    @Column(name = "product_segment_custom_text", length = 255)     // ← CHANGED: Long → String
    private String productSegmentCustomText;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_intelligence", length = 100)
    private YesNo repeatIntelligence;

    @Enumerated(EnumType.STRING)
    @Column(name = "multi_brand_risk", length = 100)
    private YesNo multiBrandRisk;

    // ── Step 4: Verification ──────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_intel_corroboration", length = 100)
    private VerificationStatus verificationIntelCorroboration;

    @Column(name = "verification_intel_corroboration_notes", columnDefinition = "TEXT")
    private String verificationIntelCorroborationNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_osint", length = 100)
    private VerificationStatus verificationOsint;

    @Column(name = "verification_osint_notes", columnDefinition = "TEXT")
    private String verificationOsintNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_pattern_mapping", length = 100)
    private VerificationStatus verificationPatternMapping;

    @Column(name = "verification_pattern_mapping_notes", columnDefinition = "TEXT")
    private String verificationPatternMappingNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_jurisdiction", length = 100)
    private VerificationStatus verificationJurisdiction;

    @Column(name = "verification_jurisdiction_notes", columnDefinition = "TEXT")
    private String verificationJurisdictionNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_risk_assessment", length = 100)
    private VerificationStatus verificationRiskAssessment;

    @Column(name = "verification_risk_assessment_notes", columnDefinition = "TEXT")
    private String verificationRiskAssessmentNotes;

    // ── Step 5: Observations ──────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "obs_operation_scale", length = 100)
    private OperationScale obsOperationScale;

    @Enumerated(EnumType.STRING)
    @Column(name = "obs_counterfeit_likelihood", length = 100)
    private LikelihoodLevel obsCounterfeitLikelihood;

    @Enumerated(EnumType.STRING)
    @Column(name = "obs_brand_exposure", length = 100)
    private BrandExposure obsBrandExposure;

    @Column(name = "obs_brand_exposure_custom_text", length = 255)  // ← CHANGED: Long → String
    private String obsBrandExposureCustomText;

    @Enumerated(EnumType.STRING)
    @Column(name = "obs_enforcement_sensitivity", length = 100)
    private RiskLevel obsEnforcementSensitivity;

    // ── Step 6: Risk Assessment ───────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_source_reliability", length = 100)
    private SourceReliability riskSourceReliability;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_client_conflict", length = 100)
    private RiskLevel riskClientConflict;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_immediate_action", length = 100)
    private YesNo riskImmediateAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_controlled_validation", length = 100)
    private YesNo riskControlledValidation;

    // ── Step 7: Assessment ────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_overall", length = 100)
    private TrueBuddyLeadAssessment assessmentOverall;

    @Column(name = "assessment_rationale", columnDefinition = "LONGTEXT")
    private String assessmentRationale;

    // ── Step 8: Way Forward ───────────────────────────────────────────────────

    @Column(name = "rec_covert_validation", length = 100)
    @Builder.Default
    private Boolean recCovertValidation = false;

    @Column(name = "rec_etp", length = 100)
    @Builder.Default
    private Boolean recEtp = false;

    @Column(name = "rec_market_reconnaissance", length = 100)
    @Builder.Default
    private Boolean recMarketReconnaissance = false;

    @Column(name = "rec_enforcement_deferred", length = 100)
    @Builder.Default
    private Boolean recEnforcementDeferred = false;

    @Column(name = "rec_continued_monitoring", length = 100)
    @Builder.Default
    private Boolean recContinuedMonitoring = false;

    @Column(name = "rec_client_segregation", length = 100)
    @Builder.Default
    private Boolean recClientSegregation = false;

    // ── Step 9: Remarks ───────────────────────────────────────────────────────

    @Column(name = "remarks", columnDefinition = "LONGTEXT")
    private String remarks;

    // ── Step 10: Disclaimer ───────────────────────────────────────────────────

    @Column(name = "custom_disclaimer", columnDefinition = "LONGTEXT")
    private String customDisclaimer;

    // ── Audit ─────────────────────────────────────────────────────────────────

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", length = 100)
    private LocalDateTime updatedAt;

    // ── Custom option per-report JSON columns ─────────────────────────────────

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scope_custom_ids", columnDefinition = "json")
    private List<Long> scopeCustomIds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "verification_custom_data", columnDefinition = "json")
    private List<com.tbcpl.workforce.operation.prereport.dto.request.CustomVerificationEntry>
            verificationCustomData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rec_custom_ids", columnDefinition = "json")
    private List<Long> recCustomIds;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "observations_custom_data", columnDefinition = "json")
    private List<com.tbcpl.workforce.operation.prereport.dto.request.CustomObservationEntry>
            observationsCustomData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "risk_custom_data", columnDefinition = "json")
    private List<com.tbcpl.workforce.operation.prereport.dto.request.CustomRiskEntry>
            riskCustomData;

    // ── Lifecycle Hooks ───────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}