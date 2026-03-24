package com.tbcpl.workforce.admin.proposal.dto.response;

import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalServiceType;
import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProposalDetailResponse {
    // ── Main ──────────────────────────────────────────
    private Long proposalId;
    private String proposalCode;
    private Long clientId;
    private String clientName;
    private String clientCompanyType;
    private String suspectEntityName;
    private String suspectEntityType;
    private String projectTitle;
    private LocalDate proposalDate;
    private String targetProducts;
    private ProposalServiceType serviceType;
    private String serviceTypeDisplayName;
    private ProposalStatus status;
    private String preparedBy;
    private String signatureStampPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    // ── Steps ─────────────────────────────────────────
    private List<ProposalStepStatusResponse> steps;

    // ── Step data ─────────────────────────────────────
    private ProposalBackgroundResponse     background;
    private ProposalScopeResponse          scopeOfWork;
    private ProposalMethodologyResponse    methodology;
    private ProposalFeeResponse            professionalFee;
    private ProposalPaymentTermsResponse   paymentTerms;
    private ProposalConfidentialityResponse confidentiality;
    private ProposalObligationsResponse    specialObligations;
    private ProposalConclusionResponse     conclusion;
}
