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
public class ClientLeadStep4Request {

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
}
