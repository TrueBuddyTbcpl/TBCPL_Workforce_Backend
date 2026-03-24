package com.tbcpl.workforce.admin.proposal.controller;

import com.tbcpl.workforce.admin.proposal.dto.request.*;
import com.tbcpl.workforce.admin.proposal.dto.response.*;
import com.tbcpl.workforce.admin.proposal.service.ProposalService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.ADMIN_BASE)
@RequiredArgsConstructor
@Slf4j
public class ProposalController {

    private final ProposalService proposalService;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    /**
     * Create a new proposal (Step 1 / Main).
     * Status defaults to DRAFT on creation.
     * All 9 step statuses are initialized to NOT_COMPLETED.
     */
    @PostMapping(ApiEndpoints.PROPOSALS)
    public ResponseEntity<ApiResponse<ProposalSummaryResponse>> createProposal(
            @Valid @RequestBody CreateProposalRequest request) {

        log.info("POST {} - Create proposal for clientId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSALS, request.getClientId());

        ProposalSummaryResponse response = proposalService.createProposal(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Proposal created successfully", response));
    }

    /**
     * Paginated list of all active proposals.
     * Default: page=0, size=10, sorted by createdAt DESC.
     */
    @GetMapping(ApiEndpoints.PROPOSALS)
    public ResponseEntity<ApiResponse<Page<ProposalSummaryResponse>>> getAllProposals(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("GET {} - List proposals page={} size={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSALS, page, size);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProposalSummaryResponse> response = proposalService.getAllProposals(pageable);
        return ResponseEntity.ok(ApiResponse.success("Proposals retrieved successfully", response));
    }

    /**
     * Full proposal detail — includes all filled step data and step statuses.
     */
    @GetMapping(ApiEndpoints.PROPOSAL_BY_ID)
    public ResponseEntity<ApiResponse<ProposalDetailResponse>> getProposalById(
            @PathVariable Long id) {

        log.info("GET {} - Get proposal id={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_BY_ID, id);

        ProposalDetailResponse response = proposalService.getProposalById(id);
        return ResponseEntity.ok(ApiResponse.success("Proposal retrieved successfully", response));
    }

    /**
     * Update Step 1 (main proposal fields).
     * Re-evaluates MAIN step status after update.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_BY_ID)
    public ResponseEntity<ApiResponse<ProposalSummaryResponse>> updateProposal(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProposalRequest request) {

        log.info("PUT {} - Update proposal id={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_BY_ID, id);

        ProposalSummaryResponse response = proposalService.updateProposal(id, request);
        return ResponseEntity.ok(ApiResponse.success("Proposal updated successfully", response));
    }

    /**
     * Soft delete a proposal.
     */
    @DeleteMapping(ApiEndpoints.PROPOSAL_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteProposal(@PathVariable Long id) {

        log.info("DELETE {} - Soft delete proposal id={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_BY_ID, id);

        proposalService.deleteProposal(id);
        return ResponseEntity.ok(ApiResponse.success("Proposal deleted successfully"));
    }

    // ── Step-wise Saves ───────────────────────────────────────────────────────

    /**
     * Step 2 — Save/update Background section.
     * Step status → COMPLETED if mode + text present, else NOT_COMPLETED.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_BACKGROUND)
    public ResponseEntity<ApiResponse<ProposalBackgroundResponse>> saveBackground(
            @PathVariable Long id,
            @Valid @RequestBody ProposalBackgroundRequest request) {

        log.info("PUT {} - Save background for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_BACKGROUND, id);

        ProposalBackgroundResponse response = proposalService.saveBackground(id, request);
        return ResponseEntity.ok(ApiResponse.success("Background saved successfully", response));
    }

    /**
     * Step 3 — Save/update Scope of Work.
     * Step status → COMPLETED if at least one scope item is selected, else NOT_COMPLETED.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_SCOPE)
    public ResponseEntity<ApiResponse<ProposalScopeResponse>> saveScope(
            @PathVariable Long id,
            @RequestBody ProposalScopeRequest request) {

        log.info("PUT {} - Save scope for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_SCOPE, id);

        ProposalScopeResponse response = proposalService.saveScope(id, request);
        return ResponseEntity.ok(ApiResponse.success("Scope of work saved successfully", response));
    }

    /**
     * Step 4 — Save/update Approach & Methodology.
     * Step status → COMPLETED if at least one methodology section has points, else NOT_COMPLETED.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_METHODOLOGY)
    public ResponseEntity<ApiResponse<ProposalMethodologyResponse>> saveMethodology(
            @PathVariable Long id,
            @RequestBody ProposalMethodologyRequest request) {

        log.info("PUT {} - Save methodology for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_METHODOLOGY, id);

        ProposalMethodologyResponse response = proposalService.saveMethodology(id, request);
        return ResponseEntity.ok(ApiResponse.success("Approach & Methodology saved successfully", response));
    }

    /**
     * Step 5 — Save/update Professional Fee.
     * Step status → COMPLETED if dueDiligenceFeeAmount is present, else NOT_COMPLETED.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_FEE)
    public ResponseEntity<ApiResponse<ProposalFeeResponse>> saveFee(
            @PathVariable Long id,
            @RequestBody ProposalFeeRequest request) {

        log.info("PUT {} - Save fee for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_FEE, id);

        ProposalFeeResponse response = proposalService.saveFee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Professional fee saved successfully", response));
    }

    /**
     * Step 6 — Save/update Payment Terms.
     * Step status → COMPLETED if paymentTermsText is non-blank, else NOT_COMPLETED.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_PAYMENT_TERMS)
    public ResponseEntity<ApiResponse<ProposalPaymentTermsResponse>> savePaymentTerms(
            @PathVariable Long id,
            @RequestBody ProposalPaymentTermsRequest request) {

        log.info("PUT {} - Save payment terms for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_PAYMENT_TERMS, id);

        ProposalPaymentTermsResponse response = proposalService.savePaymentTerms(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment terms saved successfully", response));
    }

    /**
     * Step 7 — Save/update Confidentiality.
     * Step status → COMPLETED if mode + text present, else NOT_COMPLETED.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_CONFIDENTIALITY)
    public ResponseEntity<ApiResponse<ProposalConfidentialityResponse>> saveConfidentiality(
            @PathVariable Long id,
            @Valid @RequestBody ProposalConfidentialityRequest request) {

        log.info("PUT {} - Save confidentiality for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_CONFIDENTIALITY, id);

        ProposalConfidentialityResponse response = proposalService.saveConfidentiality(id, request);
        return ResponseEntity.ok(ApiResponse.success("Confidentiality saved successfully", response));
    }

    /**
     * Step 8 — Save/update Special Obligations.
     * Step status → COMPLETED if at least one obligation point present, else NOT_COMPLETED.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_OBLIGATIONS)
    public ResponseEntity<ApiResponse<ProposalObligationsResponse>> saveObligations(
            @PathVariable Long id,
            @RequestBody ProposalObligationsRequest request) {

        log.info("PUT {} - Save obligations for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_OBLIGATIONS, id);

        ProposalObligationsResponse response = proposalService.saveObligations(id, request);
        return ResponseEntity.ok(ApiResponse.success("Special obligations saved successfully", response));
    }

    /**
     * Step 9 — Save/update Conclusion.
     * Step status → COMPLETED if mode + text present, else NOT_COMPLETED.
     */
    @PutMapping(ApiEndpoints.PROPOSAL_CONCLUSION)
    public ResponseEntity<ApiResponse<ProposalConclusionResponse>> saveConclusion(
            @PathVariable Long id,
            @Valid @RequestBody ProposalConclusionRequest request) {

        log.info("PUT {} - Save conclusion for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_CONCLUSION, id);

        ProposalConclusionResponse response = proposalService.saveConclusion(id, request);
        return ResponseEntity.ok(ApiResponse.success("Conclusion saved successfully", response));
    }

    // ── Step Tracking ─────────────────────────────────────────────────────────

    /**
     * Get all 9 step statuses for a proposal (used by frontend breadcrumb).
     */
    @GetMapping(ApiEndpoints.PROPOSAL_STEPS)
    public ResponseEntity<ApiResponse<List<ProposalStepStatusResponse>>> getStepStatuses(
            @PathVariable Long id) {

        log.info("GET {} - Get step statuses for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_STEPS, id);

        List<ProposalStepStatusResponse> response = proposalService.getStepStatuses(id);
        return ResponseEntity.ok(ApiResponse.success("Step statuses retrieved successfully", response));
    }

    // ── Admin-Only Operations ─────────────────────────────────────────────────

    /**
     * Admin-only: Change proposal status.
     * DRAFT → IN_PROGRESS → WAITING_FOR_APPROVAL → APPROVED / DECLINED / REQUEST_FOR_CHANGES.
     * remarks required when status = REQUEST_FOR_CHANGES.
     */
    @PatchMapping(ApiEndpoints.PROPOSAL_STATUS)
    public ResponseEntity<ApiResponse<ProposalSummaryResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProposalStatusUpdateRequest request) {

        log.info("PATCH {} - Update status for proposalId={} to {}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_STATUS, id, request.getStatus());

        ProposalSummaryResponse response = proposalService.updateStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success("Proposal status updated successfully", response));
    }

    /**
     * Admin-only: Upload CEO signature/stamp image.
     * Stored in S3 under proposals/signatures/.
     * Path saved in proposal.signatureStampPath.
     */
    @PostMapping(
            value    = ApiEndpoints.PROPOSAL_SIGNATURE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<ProposalSummaryResponse>> uploadSignatureStamp(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("POST {} - Upload signature stamp for proposalId={}",
                ApiEndpoints.ADMIN_BASE + ApiEndpoints.PROPOSAL_SIGNATURE, id);

        ProposalSummaryResponse response = proposalService.uploadSignatureStamp(id, file);
        return ResponseEntity.ok(ApiResponse.success("Signature stamp uploaded successfully", response));
    }
}
