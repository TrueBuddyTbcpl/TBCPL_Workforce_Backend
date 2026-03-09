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
@Table(name = "prereport_client_lead")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReportClientLead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prereport_id", unique = true, nullable = false)
    private Long prereportId;

    // Step 1: Client & Case Details
    @Column(name = "date_info_received")
    private LocalDate dateInfoReceived;

    @Column(name = "client_spoc_name")
    private String clientSpocName;

    @Column(name = "client_spoc_contact", length = 50)
    private String clientSpocContact;

    // Step 2: Mandate/Scope
    @Column(name = "scope_due_diligence")
    @Builder.Default
    private Boolean scopeDueDiligence = false;

    @Column(name = "scope_ipr_retailer")
    @Builder.Default
    private Boolean scopeIprRetailer = false;

    @Column(name = "scope_ipr_supplier")
    @Builder.Default
    private Boolean scopeIprSupplier = false;

    @Column(name = "scope_ipr_manufacturer")
    @Builder.Default
    private Boolean scopeIprManufacturer = false;

    @Column(name = "scope_online_purchase")
    @Builder.Default
    private Boolean scopeOnlinePurchase = false;

    @Column(name = "scope_offline_purchase")
    @Builder.Default
    private Boolean scopeOfflinePurchase = false;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "scope_custom_ids", columnDefinition = "json")
    private List<Long> scopeCustomIds;

    // Step 3: Information Received
    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "suspect_name")
    private String suspectName;

    @Column(name = "contact_numbers", columnDefinition = "TEXT")
    private String contactNumbers;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 20)
    private String pincode;

    @Column(name = "product_details", columnDefinition = "LONGTEXT")
    private String productDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "photos_provided")
    private YesNo photosProvided;

    @Enumerated(EnumType.STRING)
    @Column(name = "video_provided")
    private YesNo videoProvided;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_available")
    private YesNo invoiceAvailable;

    @Column(name = "source_narrative", columnDefinition = "LONGTEXT")
    private String sourceNarrative;

    // Step 4: Verification
    @Enumerated(EnumType.STRING)
    @Column(name = "verification_client_discussion")
    private VerificationStatus verificationClientDiscussion;

    @Column(name = "verification_client_discussion_notes", columnDefinition = "TEXT")
    private String verificationClientDiscussionNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_osint")
    private VerificationStatus verificationOsint;

    @Column(name = "verification_osint_notes", columnDefinition = "TEXT")
    private String verificationOsintNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_marketplace")
    private VerificationStatus verificationMarketplace;

    @Column(name = "verification_marketplace_notes", columnDefinition = "TEXT")
    private String verificationMarketplaceNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_pretext_calling")
    private VerificationStatus verificationPretextCalling;

    @Column(name = "verification_pretext_calling_notes", columnDefinition = "TEXT")
    private String verificationPretextCallingNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_product_review")
    private VerificationStatus verificationProductReview;

    @Column(name = "verification_product_review_notes", columnDefinition = "TEXT")
    private String verificationProductReviewNotes;

    // Step 5: Key Observations
    @Column(name = "obs_identifiable_target", columnDefinition = "LONGTEXT")
    private String obsIdentifiableTarget;

    @Column(name = "obs_traceability", columnDefinition = "LONGTEXT")
    private String obsTraceability;

    @Column(name = "obs_product_visibility", columnDefinition = "LONGTEXT")
    private String obsProductVisibility;

    @Column(name = "obs_counterfeiting_indications", columnDefinition = "LONGTEXT")
    private String obsCounterfeitingIndications;

    @Column(name = "obs_evidentiary_gaps", columnDefinition = "LONGTEXT")
    private String obsEvidentiary_gaps;

    // Step 6: Quality Assessment
    @Enumerated(EnumType.STRING)
    @Column(name = "qa_completeness")
    private CompletenessLevel qaCompleteness;

    @Enumerated(EnumType.STRING)
    @Column(name = "qa_accuracy")
    private AccuracyLevel qaAccuracy;

    @Enumerated(EnumType.STRING)
    @Column(name = "qa_independent_investigation")
    private YesNo qaIndependentInvestigation;

    @Enumerated(EnumType.STRING)
    @Column(name = "qa_prior_confrontation")
    private YesNo qaPriorConfrontation;

    @Enumerated(EnumType.STRING)
    @Column(name = "qa_contamination_risk")
    private RiskLevel qaContaminationRisk;

    // Step 7: Assessment
    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_overall")
    private ClientLeadAssessment assessmentOverall;

    @Column(name = "assessment_rationale", columnDefinition = "LONGTEXT")
    private String assessmentRationale;

    // Step 8: Way Forward
    @Column(name = "rec_market_survey")
    @Builder.Default
    private Boolean recMarketSurvey = false;

    @Column(name = "rec_covert_investigation")
    @Builder.Default
    private Boolean recCovertInvestigation = false;

    @Column(name = "rec_test_purchase")
    @Builder.Default
    private Boolean recTestPurchase = false;

    @Column(name = "rec_enforcement_action")
    @Builder.Default
    private Boolean recEnforcementAction = false;

    @Column(name = "rec_additional_info")
    @Builder.Default
    private Boolean recAdditionalInfo = false;

    @Column(name = "rec_closure_hold")
    @Builder.Default
    private Boolean recClosureHold = false;

    // Step 9: Remarks
    @Column(name = "remarks", columnDefinition = "LONGTEXT")
    private String remarks;

    // Step 10: Disclaimer
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
