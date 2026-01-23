package com.tbcpl.workforce.operation.prereport.dto.response;

import com.tbcpl.workforce.operation.prereport.entity.PreReportOnlinePresence;
import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLeadStepResponse {

    private Long id;
    private Long prereportId;

    // Step 1
    private LocalDate dateInfoReceived;
    private String clientSpocName;
    private String clientSpocContact;

    // Step 2
    private Boolean scopeDueDiligence;
    private Boolean scopeIprRetailer;
    private Boolean scopeIprSupplier;
    private Boolean scopeIprManufacturer;
    private Boolean scopeOnlinePurchase;
    private Boolean scopeOfflinePurchase;
    private List<Long> scopeCustomIds;

    // Step 3
    private String entityName;
    private String suspectName;
    private String contactNumbers;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private List<PreReportOnlinePresence> onlinePresences;
    private String productDetails;
    private YesNo photosProvided;
    private YesNo videoProvided;
    private YesNo invoiceAvailable;
    private String sourceNarrative;

    // Step 4
    private VerificationStatus verificationClientDiscussion;
    private String verificationClientDiscussionNotes;
    private VerificationStatus verificationOsint;
    private String verificationOsintNotes;
    private VerificationStatus verificationMarketplace;
    private String verificationMarketplaceNotes;
    private VerificationStatus verificationPretextCalling;
    private String verificationPretextCallingNotes;
    private VerificationStatus verificationProductReview;
    private String verificationProductReviewNotes;

    // Step 5
    private String obsIdentifiableTarget;
    private String obsTraceability;
    private String obsProductVisibility;
    private String obsCounterfeitingIndications;
    private String obsEvidentiary_gaps;

    // Step 6
    private CompletenessLevel qaCompleteness;
    private AccuracyLevel qaAccuracy;
    private YesNo qaIndependentInvestigation;
    private YesNo qaPriorConfrontation;
    private RiskLevel qaContaminationRisk;

    // Step 7
    private ClientLeadAssessment assessmentOverall;
    private String assessmentRationale;

    // Step 8
    private Boolean recMarketSurvey;
    private Boolean recCovertInvestigation;
    private Boolean recTestPurchase;
    private Boolean recEnforcementAction;
    private Boolean recAdditionalInfo;
    private Boolean recClosureHold;

    // Step 9
    private String remarks;

    // Step 10
    private String customDisclaimer;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
