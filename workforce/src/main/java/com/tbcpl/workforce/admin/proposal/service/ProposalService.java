package com.tbcpl.workforce.admin.proposal.service;

import com.tbcpl.workforce.admin.proposal.dto.request.*;
import com.tbcpl.workforce.admin.proposal.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProposalService {

    ProposalSummaryResponse   createProposal(CreateProposalRequest request);
    ProposalSummaryResponse   updateProposal(Long proposalId, UpdateProposalRequest request);
    ProposalDetailResponse    getProposalById(Long proposalId);
    Page<ProposalSummaryResponse> getAllProposals(Pageable pageable);
    void                      deleteProposal(Long proposalId);

    // Step saves
    ProposalBackgroundResponse     saveBackground(Long proposalId, ProposalBackgroundRequest request);
    ProposalScopeResponse          saveScope(Long proposalId, ProposalScopeRequest request);
    ProposalMethodologyResponse    saveMethodology(Long proposalId, ProposalMethodologyRequest request);
    ProposalFeeResponse            saveFee(Long proposalId, ProposalFeeRequest request);
    ProposalPaymentTermsResponse   savePaymentTerms(Long proposalId, ProposalPaymentTermsRequest request);
    ProposalConfidentialityResponse saveConfidentiality(Long proposalId, ProposalConfidentialityRequest request);
    ProposalObligationsResponse    saveObligations(Long proposalId, ProposalObligationsRequest request);
    ProposalConclusionResponse     saveConclusion(Long proposalId, ProposalConclusionRequest request);

    // Step tracking
    List<ProposalStepStatusResponse> getStepStatuses(Long proposalId);

    // Admin-only
    ProposalSummaryResponse updateStatus(Long proposalId, ProposalStatusUpdateRequest request);
    ProposalSummaryResponse uploadSignatureStamp(Long proposalId, MultipartFile file) throws IOException;
}
