package com.tbcpl.workforce.operation.prereport.entity;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    // Step 1: Case Reference
    @Column(name = "date_internal_lead_generation")
    private LocalDate dateInternalLeadGeneration;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_category")
    private ProductCategory productCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "infringement_type")
    private InfringementType infringementType;

    @Column(name = "broad_geography", columnDefinition = "TEXT")
    private String broadGeography;

    @Column(name = "client_spoc_name")
    private String clientSpocName;

    @Column(name = "client_spoc_designation")
    private String clientSpocDesignation;

    @Enumerated(EnumType.STRING)
    @Column(name = "nature_of_entity")
    private NatureOfEntity natureOfEntity;

    // Step 2: Scope Proposed
    @Column(name = "scope_ipr_supplier")
    @Builder.Default
    private Boolean scopeIprSupplier = false;

    @Column(name = "scope_ipr_manufacturer")
    @Builder.Default
    private Boolean scopeIprManufacturer = false;

    @Column(name = "scope_ipr_stockist")
    @Builder.Default
    private Boolean scopeIprStockist = false;

    @Column(name = "scope_market_verification")
    @Builder.Default
    private Boolean scopeMarketVerification = false;

    @Column(name = "scope_etp")
    @Builder.Default
    private Boolean scopeEtp = false;

    @Column(name = "scope_enforcement")
    @Builder.Default
    private Boolean scopeEnforcement = false;

    // Step 3: Lead Description
    @Enumerated(EnumType.STRING)
    @Column(name = "intel_nature")
    private IntelligenceNature intelNature;

    @Enumerated(EnumType.STRING)
    @Column(name = "suspected_activity")
    private SuspectedActivity suspectedActivity;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_segment")
    private ProductSegment productSegment;

    @Enumerated(EnumType.STRING)
    @Column(name = "supply_chain_stage")
    private SupplyChainStage supplyChainStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_intelligence")
    private YesNo repeatIntelligence;

    @Enumerated(EnumType.STRING)
    @Column(name = "multi_brand_risk")
    private YesNo multiBrandRisk;

    // Step 4: Verification
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_intel_corroboration")
    private VerificationStatus verificationIntelCorroboration;

    @Column(name = "verification_intel_corroboration_notes", columnDefinition = "TEXT")
    private String verificationIntelCorroborationNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_osint")
    private VerificationStatus verificationOsint;

    @Column(name = "verification_osint_notes", columnDefinition = "TEXT")
    private String verificationOsintNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_pattern_mapping")
    private VerificationStatus verificationPatternMapping;

    @Column(name = "verification_pattern_mapping_notes", columnDefinition = "TEXT")
    private String verificationPatternMappingNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_jurisdiction")
    private VerificationStatus verificationJurisdiction;

    @Column(name = "verification_jurisdiction_notes", columnDefinition = "TEXT")
    private String verificationJurisdictionNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_risk_assessment")
    private VerificationStatus verificationRiskAssessment;

    @Column(name = "verification_risk_assessment_notes", columnDefinition = "TEXT")
    private String verificationRiskAssessmentNotes;

    // Step 5: Observations
    @Enumerated(EnumType.STRING)
    @Column(name = "obs_operation_scale")
    private OperationScale obsOperationScale;

    @Enumerated(EnumType.STRING)
    @Column(name = "obs_counterfeit_likelihood")
    private LikelihoodLevel obsCounterfeitLikelihood;

    @Enumerated(EnumType.STRING)
    @Column(name = "obs_brand_exposure")
    private BrandExposure obsBrandExposure;

    @Enumerated(EnumType.STRING)
    @Column(name = "obs_enforcement_sensitivity")
    private RiskLevel obsEnforcementSensitivity;

    @Enumerated(EnumType.STRING)
    @Column(name = "obs_leakage_risk")
    private RiskLevel obsLeakageRisk;

    // Step 6: Risk Assessment
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_source_reliability")
    private SourceReliability riskSourceReliability;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_client_conflict")
    private RiskLevel riskClientConflict;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_immediate_action")
    private YesNo riskImmediateAction;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_controlled_validation")
    private YesNo riskControlledValidation;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_premature_disclosure")
    private RiskLevel riskPrematureDisclosure;

    // Step 7: Assessment
    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_overall")
    private TrueBuddyLeadAssessment assessmentOverall;

    @Column(name = "assessment_rationale", columnDefinition = "LONGTEXT")
    private String assessmentRationale;

    // Step 8: Way Forward
    @Column(name = "rec_covert_validation")
    @Builder.Default
    private Boolean recCovertValidation = false;

    @Column(name = "rec_etp")
    @Builder.Default
    private Boolean recEtp = false;

    @Column(name = "rec_market_reconnaissance")
    @Builder.Default
    private Boolean recMarketReconnaissance = false;

    @Column(name = "rec_enforcement_deferred")
    @Builder.Default
    private Boolean recEnforcementDeferred = false;

    @Column(name = "rec_continued_monitoring")
    @Builder.Default
    private Boolean recContinuedMonitoring = false;

    @Column(name = "rec_client_segregation")
    @Builder.Default
    private Boolean recClientSegregation = false;

    // Step 9: Confidentiality
    @Column(name = "confidentiality_note", columnDefinition = "LONGTEXT")
    private String confidentialityNote;

    // Step 10: Remarks
    @Column(name = "remarks", columnDefinition = "LONGTEXT")
    private String remarks;

    // Step 11: Disclaimer
    @Column(name = "custom_disclaimer", columnDefinition = "LONGTEXT")
    private String customDisclaimer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
