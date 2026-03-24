package com.tbcpl.workforce.admin.proposal.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.admin.proposal.ProposalDefaultTexts;
import com.tbcpl.workforce.admin.proposal.dto.inner.FeeComponentDto;
import com.tbcpl.workforce.admin.proposal.dto.inner.ScopeItemDto;
import com.tbcpl.workforce.admin.proposal.dto.request.*;
import com.tbcpl.workforce.admin.proposal.dto.response.*;
import com.tbcpl.workforce.admin.proposal.entity.*;
import com.tbcpl.workforce.admin.proposal.entity.enums.*;
import com.tbcpl.workforce.admin.proposal.repository.*;
import com.tbcpl.workforce.admin.proposal.service.ProposalService;
import com.tbcpl.workforce.admin.repository.ClientRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProposalServiceImpl implements ProposalService {

    private static final String PREPARED_BY_DEFAULT = "True Buddy Consulting Pvt Ltd";

    private final ProposalRepository                proposalRepository;
    private final ProposalBackgroundRepository      backgroundRepository;
    private final ProposalScopeRepository           scopeRepository;
    private final ProposalMethodologyRepository     methodologyRepository;
    private final ProposalFeeRepository             feeRepository;
    private final ProposalPaymentTermsRepository    paymentTermsRepository;
    private final ProposalConfidentialityRepository confidentialityRepository;
    private final ProposalObligationsRepository     obligationsRepository;
    private final ProposalConclusionRepository      conclusionRepository;
    private final ProposalStepStatusRepository      stepStatusRepository;
    private final ClientRepository                  clientRepository;
    private final S3Service                         s3Service;
    private final ObjectMapper                      objectMapper;

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE PROPOSAL
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalSummaryResponse createProposal(CreateProposalRequest request) {
        log.info("Creating proposal for clientId={}", request.getClientId());

        Client client = clientRepository.findActiveClientById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        String code  = generateProposalCode();
        String actor = getCurrentActor();

        Proposal proposal = Proposal.builder()
                .proposalCode(code)
                .clientId(client.getClientId())
                .clientCompanyType(request.getClientCompanyType())
                .suspectEntityName(request.getSuspectEntityName())
                .suspectEntityType(request.getSuspectEntityType())
                .projectTitle(request.getProjectTitle())
                .proposalDate(request.getProposalDate())
                .targetProducts(request.getTargetProducts())
                .serviceType(request.getServiceType())
                .status(ProposalStatus.DRAFT)
                .preparedBy(PREPARED_BY_DEFAULT)
                .deleted(false)
                .createdBy(actor)
                .updatedBy(actor)
                .build();

        proposal = proposalRepository.save(proposal);

        // Initialize all 9 steps as NOT_COMPLETED
        initializeStepStatuses(proposal, actor);

        // Evaluate MAIN step right after creation
        evaluateMainStepStatus(proposal);

        log.info("Proposal created: code={}, id={}", code, proposal.getProposalId());
        return toSummaryResponse(proposal, client.getClientName());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE PROPOSAL (Step 1 / Main)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalSummaryResponse updateProposal(Long proposalId, UpdateProposalRequest request) {
        log.info("Updating main step for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);

        Client client = clientRepository.findActiveClientById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        String actor = getCurrentActor();
        proposal.setClientId(client.getClientId());
        proposal.setClientCompanyType(request.getClientCompanyType());
        proposal.setSuspectEntityName(request.getSuspectEntityName());
        proposal.setSuspectEntityType(request.getSuspectEntityType());
        proposal.setProjectTitle(request.getProjectTitle());
        proposal.setProposalDate(request.getProposalDate());
        proposal.setTargetProducts(request.getTargetProducts());
        proposal.setServiceType(request.getServiceType());
        proposal.setUpdatedBy(actor);

        // Bump status from DRAFT → IN_PROGRESS on first meaningful update
        if (proposal.getStatus() == ProposalStatus.DRAFT) {
            proposal.setStatus(ProposalStatus.IN_PROGRESS);
        }

        proposal = proposalRepository.save(proposal);
        evaluateMainStepStatus(proposal);

        return toSummaryResponse(proposal, client.getClientName());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET DETAIL
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public ProposalDetailResponse getProposalById(Long proposalId) {
        log.info("Fetching proposal detail for id={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);

        String clientName = clientRepository.findActiveClientById(proposal.getClientId())
                .map(Client::getClientName)
                .orElse("N/A");

        ProposalDetailResponse response = new ProposalDetailResponse();
        response.setProposalId(proposal.getProposalId());
        response.setProposalCode(proposal.getProposalCode());
        response.setClientId(proposal.getClientId());
        response.setClientName(clientName);
        response.setClientCompanyType(proposal.getClientCompanyType());
        response.setSuspectEntityName(proposal.getSuspectEntityName());
        response.setSuspectEntityType(proposal.getSuspectEntityType());
        response.setProjectTitle(proposal.getProjectTitle());
        response.setProposalDate(proposal.getProposalDate());
        response.setTargetProducts(proposal.getTargetProducts());
        response.setServiceType(proposal.getServiceType());
        response.setServiceTypeDisplayName(
                proposal.getServiceType() != null ? proposal.getServiceType().getDisplayName() : null);
        response.setStatus(proposal.getStatus());
        response.setPreparedBy(proposal.getPreparedBy());
        response.setSignatureStampPath(proposal.getSignatureStampPath());
        response.setCreatedAt(proposal.getCreatedAt());
        response.setUpdatedAt(proposal.getUpdatedAt());
        response.setCreatedBy(proposal.getCreatedBy());
        response.setSteps(getStepStatuses(proposalId));

        // Load each step's data if it exists — null if not yet filled
        backgroundRepository.findByProposal_ProposalId(proposalId)
                .ifPresent(e -> response.setBackground(toBackgroundResponse(e)));

        scopeRepository.findByProposal_ProposalId(proposalId)
                .ifPresent(e -> response.setScopeOfWork(toScopeResponse(e)));

        methodologyRepository.findByProposal_ProposalId(proposalId)
                .ifPresent(e -> response.setMethodology(toMethodologyResponse(e)));

        feeRepository.findByProposal_ProposalId(proposalId)
                .ifPresent(e -> response.setProfessionalFee(toFeeResponse(e)));

        paymentTermsRepository.findByProposal_ProposalId(proposalId)
                .ifPresent(e -> response.setPaymentTerms(toPaymentTermsResponse(e)));

        confidentialityRepository.findByProposal_ProposalId(proposalId)
                .ifPresent(e -> response.setConfidentiality(toConfidentialityResponse(e)));

        obligationsRepository.findByProposal_ProposalId(proposalId)
                .ifPresent(e -> response.setSpecialObligations(toObligationsResponse(e, clientName)));

        conclusionRepository.findByProposal_ProposalId(proposalId)
                .ifPresent(e -> response.setConclusion(toConclusionResponse(e)));

        return response;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET ALL (Paginated)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<ProposalSummaryResponse> getAllProposals(Pageable pageable) {
        log.info("Fetching all active proposals, page={}", pageable.getPageNumber());

        return proposalRepository.findAllActive(pageable)
                .map(proposal -> {
                    String clientName = clientRepository.findActiveClientById(proposal.getClientId())
                            .map(Client::getClientName)
                            .orElse("N/A");
                    return toSummaryResponse(proposal, clientName);
                });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SOFT DELETE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void deleteProposal(Long proposalId) {
        log.info("Soft deleting proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        proposal.setDeleted(true);
        proposal.setUpdatedBy(getCurrentActor());
        proposalRepository.save(proposal);

        log.info("Proposal soft deleted: id={}", proposalId);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 2 — BACKGROUND
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalBackgroundResponse saveBackground(Long proposalId, ProposalBackgroundRequest request) {
        log.info("Saving background for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        ProposalBackground background = backgroundRepository
                .findByProposal_ProposalId(proposalId)
                .orElseGet(() -> ProposalBackground.builder()
                        .proposal(proposal)
                        .createdBy(actor)
                        .build());

        // If DEFAULT mode → always store the exact default text
        String textToStore = (request.getMode() == TextMode.DEFAULT)
                ? ProposalDefaultTexts.BACKGROUND
                : request.getBackgroundText();

        background.setMode(request.getMode());
        background.setBackgroundText(textToStore);
        background.setUpdatedBy(actor);

        background = backgroundRepository.save(background);

        // Evaluate step: COMPLETED if mode is set AND text is non-blank
        boolean hasData = background.getMode() != null
                && background.getBackgroundText() != null
                && !background.getBackgroundText().isBlank();
        updateStepStatus(proposal, StepName.BACKGROUND, hasData, actor);

        return toBackgroundResponse(background);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 3 — SCOPE OF WORK
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalScopeResponse saveScope(Long proposalId, ProposalScopeRequest request) {
        log.info("Saving scope of work for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        ProposalScopeOfWork scope = scopeRepository
                .findByProposal_ProposalId(proposalId)
                .orElseGet(() -> ProposalScopeOfWork.builder()
                        .proposal(proposal)
                        .createdBy(actor)
                        .build());

        scope.setScopeItemsJson(toJson(request.getScopeItems()));
        scope.setUpdatedBy(actor);

        scope = scopeRepository.save(scope);

        // COMPLETED if at least one item exists in the list
        boolean hasData = request.getScopeItems() != null
                && !request.getScopeItems().isEmpty();
        updateStepStatus(proposal, StepName.SCOPE_OF_WORK, hasData, actor);

        return toScopeResponse(scope);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 4 — APPROACH & METHODOLOGY
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalMethodologyResponse saveMethodology(Long proposalId, ProposalMethodologyRequest request) {
        log.info("Saving methodology for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        ProposalApproachMethodology methodology = methodologyRepository
                .findByProposal_ProposalId(proposalId)
                .orElseGet(() -> ProposalApproachMethodology.builder()
                        .proposal(proposal)
                        .createdBy(actor)
                        .build());

        methodology.setDesktopDueDiligencePoints(toJson(request.getDesktopDueDiligencePoints()));
        methodology.setMarketGroundIntelligencePoints(toJson(request.getMarketGroundIntelligencePoints()));
        methodology.setProductVerificationPoints(toJson(request.getProductVerificationPoints()));
        methodology.setTestPurchasePoints(toJson(request.getTestPurchasePoints()));
        methodology.setUpdatedBy(actor);

        methodology = methodologyRepository.save(methodology);

        // COMPLETED if at least one section has at least one point
        boolean hasData = hasContent(request.getDesktopDueDiligencePoints())
                || hasContent(request.getMarketGroundIntelligencePoints())
                || hasContent(request.getProductVerificationPoints())
                || hasContent(request.getTestPurchasePoints());
        updateStepStatus(proposal, StepName.APPROACH_METHODOLOGY, hasData, actor);

        return toMethodologyResponse(methodology);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 5 — PROFESSIONAL FEE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalFeeResponse saveFee(Long proposalId, ProposalFeeRequest request) {
        log.info("Saving professional fee for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        ProposalProfessionalFee fee = feeRepository
                .findByProposal_ProposalId(proposalId)
                .orElseGet(() -> ProposalProfessionalFee.builder()
                        .proposal(proposal)
                        .createdBy(actor)
                        .build());

        fee.setDueDiligenceFeeAmount(request.getDueDiligenceFeeAmount());
        fee.setFeeComponentsJson(toJson(request.getFeeComponents()));
        fee.setSpecialConditionsJson(toJson(request.getSpecialConditions()));
        fee.setUpdatedBy(actor);

        fee = feeRepository.save(fee);

        // COMPLETED only if the due diligence fee amount is provided
        boolean hasData = request.getDueDiligenceFeeAmount() != null;
        updateStepStatus(proposal, StepName.PROFESSIONAL_FEE, hasData, actor);

        return toFeeResponse(fee);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 6 — PAYMENT TERMS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalPaymentTermsResponse savePaymentTerms(Long proposalId, ProposalPaymentTermsRequest request) {
        log.info("Saving payment terms for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        ProposalPaymentTerms paymentTerms = paymentTermsRepository
                .findByProposal_ProposalId(proposalId)
                .orElseGet(() -> ProposalPaymentTerms.builder()
                        .proposal(proposal)
                        .createdBy(actor)
                        .build());

        paymentTerms.setPaymentTermsText(request.getPaymentTermsText());
        paymentTerms.setUpdatedBy(actor);

        paymentTerms = paymentTermsRepository.save(paymentTerms);

        // COMPLETED if text is non-blank
        boolean hasData = request.getPaymentTermsText() != null
                && !request.getPaymentTermsText().isBlank();
        updateStepStatus(proposal, StepName.PAYMENT_TERMS, hasData, actor);

        return toPaymentTermsResponse(paymentTerms);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 7 — CONFIDENTIALITY
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalConfidentialityResponse saveConfidentiality(Long proposalId, ProposalConfidentialityRequest request) {
        log.info("Saving confidentiality for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        ProposalConfidentiality confidentiality = confidentialityRepository
                .findByProposal_ProposalId(proposalId)
                .orElseGet(() -> ProposalConfidentiality.builder()
                        .proposal(proposal)
                        .createdBy(actor)
                        .build());

        // DEFAULT mode → always store exact default text
        String textToStore = (request.getParagraphMode() == TextMode.DEFAULT)
                ? ProposalDefaultTexts.CONFIDENTIALITY
                : request.getParagraphText();

        confidentiality.setParagraphMode(request.getParagraphMode());
        confidentiality.setParagraphText(textToStore);
        confidentiality.setCustomPointsJson(toJson(request.getCustomPoints()));
        confidentiality.setUpdatedBy(actor);

        confidentiality = confidentialityRepository.save(confidentiality);

        // COMPLETED if mode is set AND paragraph text is non-blank
        boolean hasData = confidentiality.getParagraphMode() != null
                && confidentiality.getParagraphText() != null
                && !confidentiality.getParagraphText().isBlank();
        updateStepStatus(proposal, StepName.CONFIDENTIALITY, hasData, actor);

        return toConfidentialityResponse(confidentiality);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 8 — SPECIAL OBLIGATIONS
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalObligationsResponse saveObligations(Long proposalId, ProposalObligationsRequest request) {
        log.info("Saving special obligations for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        String clientName = clientRepository.findActiveClientById(proposal.getClientId())
                .map(Client::getClientName)
                .orElse("N/A");

        ProposalSpecialObligations obligations = obligationsRepository
                .findByProposal_ProposalId(proposalId)
                .orElseGet(() -> ProposalSpecialObligations.builder()
                        .proposal(proposal)
                        .clientId(proposal.getClientId())
                        .createdBy(actor)
                        .build());

        obligations.setObligationPointsJson(toJson(request.getObligationPoints()));
        obligations.setUpdatedBy(actor);

        obligations = obligationsRepository.save(obligations);

        // COMPLETED if at least one obligation point is present
        boolean hasData = hasContent(request.getObligationPoints());
        updateStepStatus(proposal, StepName.SPECIAL_OBLIGATIONS, hasData, actor);

        return toObligationsResponse(obligations, clientName);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP 9 — CONCLUSION
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalConclusionResponse saveConclusion(Long proposalId, ProposalConclusionRequest request) {
        log.info("Saving conclusion for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        String clientName = clientRepository.findActiveClientById(proposal.getClientId())
                .map(Client::getClientName)
                .orElse("N/A");

        ProposalConclusion conclusion = conclusionRepository
                .findByProposal_ProposalId(proposalId)
                .orElseGet(() -> ProposalConclusion.builder()
                        .proposal(proposal)
                        .createdBy(actor)
                        .build());

        // DEFAULT mode → generate default conclusion text with client name injected
        String textToStore = (request.getParagraphMode() == TextMode.DEFAULT)
                ? ProposalDefaultTexts.conclusionFor(clientName)
                : request.getParagraphText();

        conclusion.setParagraphMode(request.getParagraphMode());
        conclusion.setParagraphText(textToStore);
        conclusion.setUpdatedBy(actor);

        conclusion = conclusionRepository.save(conclusion);

        // COMPLETED if mode is set AND text is non-blank
        boolean hasData = conclusion.getParagraphMode() != null
                && conclusion.getParagraphText() != null
                && !conclusion.getParagraphText().isBlank();
        updateStepStatus(proposal, StepName.CONCLUSION, hasData, actor);

        return toConclusionResponse(conclusion);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STEP STATUSES
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<ProposalStepStatusResponse> getStepStatuses(Long proposalId) {
        return stepStatusRepository.findByProposal_ProposalId(proposalId)
                .stream()
                .map(s -> new ProposalStepStatusResponse(s.getStepName(), s.getStatus()))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN — STATUS UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalSummaryResponse updateStatus(Long proposalId, ProposalStatusUpdateRequest request) {
        log.info("Updating status for proposalId={} to {}", proposalId, request.getStatus());

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        proposal.setStatus(request.getStatus());
        proposal.setUpdatedBy(actor);
        proposal = proposalRepository.save(proposal);

        String clientName = clientRepository.findActiveClientById(proposal.getClientId())
                .map(Client::getClientName)
                .orElse("N/A");

        log.info("Proposal status updated: id={}, newStatus={}", proposalId, request.getStatus());
        return toSummaryResponse(proposal, clientName);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN — SIGNATURE STAMP UPLOAD
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public ProposalSummaryResponse uploadSignatureStamp(Long proposalId, MultipartFile file) throws IOException {
        log.info("Uploading signature stamp for proposalId={}", proposalId);

        Proposal proposal = getActiveProposal(proposalId);
        String actor      = getCurrentActor();

        Map<String, String> uploaded = s3Service.uploadFile(file, "proposals/signatures");

        proposal.setSignatureStampPath(uploaded.get("url"));
        proposal.setUpdatedBy(actor);
        proposal = proposalRepository.save(proposal);

        String clientName = clientRepository.findActiveClientById(proposal.getClientId())
                .map(Client::getClientName)
                .orElse("N/A");

        log.info("Signature stamp uploaded for proposalId={}", proposalId);
        return toSummaryResponse(proposal, clientName);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE — HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Fetch a non-deleted proposal or throw ResourceNotFoundException.
     */
    private Proposal getActiveProposal(Long proposalId) {
        return proposalRepository.findActiveById(proposalId)
                .orElseThrow(() -> new ResourceNotFoundException("Proposal", "id", proposalId));
    }

    /**
     * Extract the authenticated user's empId as the actor string.
     * Falls back to "system" if no authentication context exists.
     */
    private String getCurrentActor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "system";
        }
        return auth.getName(); // Returns empId per JwtAuthenticationFilter
    }

    /**
     * Initialize all 9 ProposalStepStatus rows as NOT_COMPLETED
     * when a new proposal is created.
     */
    private void initializeStepStatuses(Proposal proposal, String actor) {
        List<ProposalStepStatus> steps = Arrays.stream(StepName.values())
                .map(stepName -> ProposalStepStatus.builder()
                        .proposal(proposal)
                        .stepName(stepName)
                        .status(StepStatus.NOT_COMPLETED)
                        .createdBy(actor)
                        .updatedBy(actor)
                        .build())
                .collect(Collectors.toList());
        stepStatusRepository.saveAll(steps);
        log.debug("Initialized {} step statuses for proposalId={}", steps.size(), proposal.getProposalId());
    }

    /**
     * Evaluate MAIN step: COMPLETED if all mandatory fields are present.
     * Mandatory: clientId, proposalDate, serviceType.
     */
    private void evaluateMainStepStatus(Proposal proposal) {
        boolean hasData = proposal.getClientId() != null
                && proposal.getProposalDate() != null
                && proposal.getServiceType() != null;
        updateStepStatus(proposal, StepName.MAIN, hasData, getCurrentActor());
    }

    /**
     * Set a specific step's status to COMPLETED or NOT_COMPLETED.
     */
    private void updateStepStatus(Proposal proposal, StepName stepName, boolean completed, String actor) {
        ProposalStepStatus stepStatus = stepStatusRepository
                .findByProposal_ProposalIdAndStepName(proposal.getProposalId(), stepName)
                .orElseGet(() -> ProposalStepStatus.builder()
                        .proposal(proposal)
                        .stepName(stepName)
                        .createdBy(actor)
                        .build());

        stepStatus.setStatus(completed ? StepStatus.COMPLETED : StepStatus.NOT_COMPLETED);
        stepStatus.setUpdatedBy(actor);
        stepStatusRepository.save(stepStatus);

        log.debug("Step {} → {} for proposalId={}",
                stepName, stepStatus.getStatus(), proposal.getProposalId());
    }

    /**
     * Generate unique proposal code in format PROP-YYNNN.
     * e.g. PROP-26001, PROP-26002 ...
     * Thread-safe via synchronized block on prefix.
     */
    private synchronized String generateProposalCode() {
        String yy     = String.valueOf(LocalDate.now().getYear()).substring(2);
        String prefix = "PROP-" + yy;

        Optional<String> maxCode = proposalRepository.findMaxProposalCodeByPrefix(prefix);

        int nextNumber = maxCode
                .map(code -> {
                    try {
                        // Extract the NNN part after "PROP-YY"
                        return Integer.parseInt(code.substring(prefix.length())) + 1;
                    } catch (NumberFormatException e) {
                        log.warn("Could not parse proposal code: {}", code);
                        return 1;
                    }
                })
                .orElse(1);

        return String.format("%s%03d", prefix, nextNumber);
    }

    /**
     * Serialize any object to JSON string.
     * Returns null if input is null.
     */
    private String toJson(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON serialization failed: {}", e.getMessage());
            throw new RuntimeException("Failed to serialize data to JSON", e);
        }
    }

    /**
     * Deserialize JSON string to List<String>.
     */
    private List<String> fromJsonStringList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("JSON deserialization to List<String> failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Deserialize JSON string to List<ScopeItemDto>.
     */
    private List<ScopeItemDto> fromJsonScopeItems(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<ScopeItemDto>>() {});
        } catch (Exception e) {
            log.error("JSON deserialization to List<ScopeItemDto> failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Deserialize JSON string to List<FeeComponentDto>.
     */
    private List<FeeComponentDto> fromJsonFeeComponents(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<FeeComponentDto>>() {});
        } catch (Exception e) {
            log.error("JSON deserialization to List<FeeComponentDto> failed: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Returns true if a list is non-null and has at least one entry.
     */
    private boolean hasContent(List<?> list) {
        return list != null && !list.isEmpty();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE — MAPPERS
    // ─────────────────────────────────────────────────────────────────────────

    private ProposalSummaryResponse toSummaryResponse(Proposal proposal, String clientName) {
        ProposalSummaryResponse response = new ProposalSummaryResponse();
        response.setProposalId(proposal.getProposalId());
        response.setProposalCode(proposal.getProposalCode());
        response.setClientId(proposal.getClientId());
        response.setClientName(clientName);
        response.setSuspectEntityName(proposal.getSuspectEntityName());
        response.setProjectTitle(proposal.getProjectTitle());
        response.setServiceType(proposal.getServiceType());
        response.setServiceTypeDisplayName(
                proposal.getServiceType() != null ? proposal.getServiceType().getDisplayName() : null);
        response.setStatus(proposal.getStatus());
        response.setProposalDate(proposal.getProposalDate());
        response.setCreatedAt(proposal.getCreatedAt());
        response.setUpdatedAt(proposal.getUpdatedAt());
        response.setCreatedBy(proposal.getCreatedBy());
        response.setSteps(getStepStatuses(proposal.getProposalId()));
        return response;
    }

    private ProposalBackgroundResponse toBackgroundResponse(ProposalBackground entity) {
        ProposalBackgroundResponse response = new ProposalBackgroundResponse();
        response.setId(entity.getId());
        response.setMode(entity.getMode());
        response.setBackgroundText(entity.getBackgroundText());
        return response;
    }

    private ProposalScopeResponse toScopeResponse(ProposalScopeOfWork entity) {
        ProposalScopeResponse response = new ProposalScopeResponse();
        response.setId(entity.getId());
        response.setScopeItems(fromJsonScopeItems(entity.getScopeItemsJson()));
        return response;
    }

    private ProposalMethodologyResponse toMethodologyResponse(ProposalApproachMethodology entity) {
        ProposalMethodologyResponse response = new ProposalMethodologyResponse();
        response.setId(entity.getId());
        response.setDesktopDueDiligencePoints(fromJsonStringList(entity.getDesktopDueDiligencePoints()));
        response.setMarketGroundIntelligencePoints(fromJsonStringList(entity.getMarketGroundIntelligencePoints()));
        response.setProductVerificationPoints(fromJsonStringList(entity.getProductVerificationPoints()));
        response.setTestPurchasePoints(fromJsonStringList(entity.getTestPurchasePoints()));
        return response;
    }

    private ProposalFeeResponse toFeeResponse(ProposalProfessionalFee entity) {
        ProposalFeeResponse response = new ProposalFeeResponse();
        response.setId(entity.getId());
        response.setDueDiligenceFeeAmount(entity.getDueDiligenceFeeAmount());
        response.setFeeComponents(fromJsonFeeComponents(entity.getFeeComponentsJson()));
        response.setSpecialConditions(fromJsonStringList(entity.getSpecialConditionsJson()));
        return response;
    }

    private ProposalPaymentTermsResponse toPaymentTermsResponse(ProposalPaymentTerms entity) {
        ProposalPaymentTermsResponse response = new ProposalPaymentTermsResponse();
        response.setId(entity.getId());
        response.setPaymentTermsText(entity.getPaymentTermsText());
        return response;
    }

    private ProposalConfidentialityResponse toConfidentialityResponse(ProposalConfidentiality entity) {
        ProposalConfidentialityResponse response = new ProposalConfidentialityResponse();
        response.setId(entity.getId());
        response.setParagraphMode(entity.getParagraphMode());
        response.setParagraphText(entity.getParagraphText());
        response.setCustomPoints(fromJsonStringList(entity.getCustomPointsJson()));
        return response;
    }

    private ProposalObligationsResponse toObligationsResponse(ProposalSpecialObligations entity, String clientName) {
        ProposalObligationsResponse response = new ProposalObligationsResponse();
        response.setId(entity.getId());
        response.setClientId(entity.getClientId());
        response.setClientName(clientName);
        response.setObligationPoints(fromJsonStringList(entity.getObligationPointsJson()));
        return response;
    }

    private ProposalConclusionResponse toConclusionResponse(ProposalConclusion entity) {
        ProposalConclusionResponse response = new ProposalConclusionResponse();
        response.setId(entity.getId());
        response.setParagraphMode(entity.getParagraphMode());
        response.setParagraphText(entity.getParagraphText());
        return response;
    }
}
