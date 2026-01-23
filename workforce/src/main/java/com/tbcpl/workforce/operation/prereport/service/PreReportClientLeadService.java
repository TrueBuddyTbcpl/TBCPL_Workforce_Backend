package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.operation.prereport.dto.request.*;
import com.tbcpl.workforce.operation.prereport.dto.response.ClientLeadStepResponse;
import com.tbcpl.workforce.operation.prereport.entity.PreReport;
import com.tbcpl.workforce.operation.prereport.entity.PreReportClientLead;
import com.tbcpl.workforce.operation.prereport.entity.PreReportOnlinePresence;
import com.tbcpl.workforce.operation.prereport.repository.PreReportClientLeadRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportOnlinePresenceRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PreReportClientLeadService {

    private final PreReportClientLeadRepository clientLeadRepository;
    private final PreReportOnlinePresenceRepository onlinePresenceRepository;
    private final PreReportRepository preReportRepository;  // ✅ ADDED
    private final PreReportService preReportService;

    public PreReportClientLeadService(PreReportClientLeadRepository clientLeadRepository,
                                      PreReportOnlinePresenceRepository onlinePresenceRepository,
                                      PreReportRepository preReportRepository,  // ✅ ADDED
                                      PreReportService preReportService) {
        this.clientLeadRepository = clientLeadRepository;
        this.onlinePresenceRepository = onlinePresenceRepository;
        this.preReportRepository = preReportRepository;  // ✅ ADDED
        this.preReportService = preReportService;
    }

    @Transactional
    public void initializeClientLead(Long prereportId) {
        log.info("Initializing client lead for prereportId: {}", prereportId);

        PreReportClientLead clientLead = PreReportClientLead.builder()
                .prereportId(prereportId)
                .build();

        clientLeadRepository.save(clientLead);
    }

    @Transactional(readOnly = true)
    public ClientLeadStepResponse getClientLeadByPrereportId(Long prereportId) {
        log.info("Fetching client lead data for prereportId: {}", prereportId);

        PreReportClientLead clientLead = clientLeadRepository.findByPrereportId(prereportId)
                .orElseThrow(() -> new RuntimeException("Client lead not found for prereport ID: " + prereportId));

        List<PreReportOnlinePresence> onlinePresences = onlinePresenceRepository.findByPrereportId(prereportId);

        return mapToResponse(clientLead, onlinePresences);
    }

    @Transactional
    public ClientLeadStepResponse updateStep1(Long prereportId, ClientLeadStep1Request request) {
        log.info("Updating client lead step 1 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setDateInfoReceived(request.getDateInfoReceived());
        clientLead.setClientSpocName(request.getClientSpocName());
        clientLead.setClientSpocContact(request.getClientSpocContact());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 1);

        return mapToResponse(clientLead, List.of());
    }

    @Transactional
    public ClientLeadStepResponse updateStep2(Long prereportId, ClientLeadStep2Request request) {
        log.info("Updating client lead step 2 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setScopeDueDiligence(request.getScopeDueDiligence());
        clientLead.setScopeIprRetailer(request.getScopeIprRetailer());
        clientLead.setScopeIprSupplier(request.getScopeIprSupplier());
        clientLead.setScopeIprManufacturer(request.getScopeIprManufacturer());
        clientLead.setScopeOnlinePurchase(request.getScopeOnlinePurchase());
        clientLead.setScopeOfflinePurchase(request.getScopeOfflinePurchase());
        clientLead.setScopeCustomIds(request.getScopeCustomIds());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 2);

        return mapToResponse(clientLead, List.of());
    }

    @Transactional
    public ClientLeadStepResponse updateStep3(Long prereportId, ClientLeadStep3Request request) {
        log.info("Updating client lead step 3 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setEntityName(request.getEntityName());
        clientLead.setSuspectName(request.getSuspectName());
        clientLead.setContactNumbers(request.getContactNumbers());
        clientLead.setAddressLine1(request.getAddressLine1());
        clientLead.setAddressLine2(request.getAddressLine2());
        clientLead.setCity(request.getCity());
        clientLead.setState(request.getState());
        clientLead.setPincode(request.getPincode());
        clientLead.setProductDetails(request.getProductDetails());
        clientLead.setPhotosProvided(request.getPhotosProvided());
        clientLead.setVideoProvided(request.getVideoProvided());
        clientLead.setInvoiceAvailable(request.getInvoiceAvailable());
        clientLead.setSourceNarrative(request.getSourceNarrative());

        clientLeadRepository.save(clientLead);

        // Handle online presences
        onlinePresenceRepository.deleteByPrereportId(prereportId);
        if (request.getOnlinePresences() != null && !request.getOnlinePresences().isEmpty()) {
            List<PreReportOnlinePresence> presences = request.getOnlinePresences().stream()
                    .map(op -> PreReportOnlinePresence.builder()
                            .prereportId(prereportId)
                            .platformName(op.getPlatformName())
                            .link(op.getLink())
                            .build())
                    .collect(Collectors.toList());
            onlinePresenceRepository.saveAll(presences);
        }

        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 3);

        return mapToResponse(clientLead, onlinePresenceRepository.findByPrereportId(prereportId));
    }

    @Transactional
    public ClientLeadStepResponse updateStep4(Long prereportId, ClientLeadStep4Request request) {
        log.info("Updating client lead step 4 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setVerificationClientDiscussion(request.getVerificationClientDiscussion());
        clientLead.setVerificationClientDiscussionNotes(request.getVerificationClientDiscussionNotes());
        clientLead.setVerificationOsint(request.getVerificationOsint());
        clientLead.setVerificationOsintNotes(request.getVerificationOsintNotes());
        clientLead.setVerificationMarketplace(request.getVerificationMarketplace());
        clientLead.setVerificationMarketplaceNotes(request.getVerificationMarketplaceNotes());
        clientLead.setVerificationPretextCalling(request.getVerificationPretextCalling());
        clientLead.setVerificationPretextCallingNotes(request.getVerificationPretextCallingNotes());
        clientLead.setVerificationProductReview(request.getVerificationProductReview());
        clientLead.setVerificationProductReviewNotes(request.getVerificationProductReviewNotes());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 4);

        return mapToResponse(clientLead, List.of());
    }

    @Transactional
    public ClientLeadStepResponse updateStep5(Long prereportId, ClientLeadStep5Request request) {
        log.info("Updating client lead step 5 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setObsIdentifiableTarget(request.getObsIdentifiableTarget());
        clientLead.setObsTraceability(request.getObsTraceability());
        clientLead.setObsProductVisibility(request.getObsProductVisibility());
        clientLead.setObsCounterfeitingIndications(request.getObsCounterfeitingIndications());
        clientLead.setObsEvidentiary_gaps(request.getObsEvidentiary_gaps());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 5);

        return mapToResponse(clientLead, List.of());
    }

    @Transactional
    public ClientLeadStepResponse updateStep6(Long prereportId, ClientLeadStep6Request request) {
        log.info("Updating client lead step 6 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setQaCompleteness(request.getQaCompleteness());
        clientLead.setQaAccuracy(request.getQaAccuracy());
        clientLead.setQaIndependentInvestigation(request.getQaIndependentInvestigation());
        clientLead.setQaPriorConfrontation(request.getQaPriorConfrontation());
        clientLead.setQaContaminationRisk(request.getQaContaminationRisk());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 6);

        return mapToResponse(clientLead, List.of());
    }

    @Transactional
    public ClientLeadStepResponse updateStep7(Long prereportId, ClientLeadStep7Request request) {
        log.info("Updating client lead step 7 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setAssessmentOverall(request.getAssessmentOverall());
        clientLead.setAssessmentRationale(request.getAssessmentRationale());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 7);

        return mapToResponse(clientLead, List.of());
    }

    @Transactional
    public ClientLeadStepResponse updateStep8(Long prereportId, ClientLeadStep8Request request) {
        log.info("Updating client lead step 8 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setRecMarketSurvey(request.getRecMarketSurvey());
        clientLead.setRecCovertInvestigation(request.getRecCovertInvestigation());
        clientLead.setRecTestPurchase(request.getRecTestPurchase());
        clientLead.setRecEnforcementAction(request.getRecEnforcementAction());
        clientLead.setRecAdditionalInfo(request.getRecAdditionalInfo());
        clientLead.setRecClosureHold(request.getRecClosureHold());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 8);

        return mapToResponse(clientLead, List.of());
    }

    @Transactional
    public ClientLeadStepResponse updateStep9(Long prereportId, ClientLeadStep9Request request) {
        log.info("Updating client lead step 9 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setRemarks(request.getRemarks());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 9);

        return mapToResponse(clientLead, List.of());
    }

    @Transactional
    public ClientLeadStepResponse updateStep10(Long prereportId, ClientLeadStep10Request request) {
        log.info("Updating client lead step 10 for prereportId: {}", prereportId);

        PreReportClientLead clientLead = getClientLeadEntity(prereportId);

        clientLead.setCustomDisclaimer(request.getCustomDisclaimer());

        clientLeadRepository.save(clientLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 10);

        return mapToResponse(clientLead, List.of());
    }

    private PreReportClientLead getClientLeadEntity(Long prereportId) {
        return clientLeadRepository.findByPrereportId(prereportId)
                .orElseThrow(() -> new RuntimeException("Client lead not found for prereport ID: " + prereportId));
    }

    // ✅ FIXED: Properly fetch reportId from PreReport table
    private String getReportIdFromPrereportId(Long prereportId) {
        PreReport preReport = preReportRepository.findById(prereportId)
                .orElseThrow(() -> new RuntimeException("PreReport not found with ID: " + prereportId));
        return preReport.getReportId();
    }

    private ClientLeadStepResponse mapToResponse(PreReportClientLead clientLead, List<PreReportOnlinePresence> onlinePresences) {
        return ClientLeadStepResponse.builder()
                .id(clientLead.getId())
                .prereportId(clientLead.getPrereportId())
                .dateInfoReceived(clientLead.getDateInfoReceived())
                .clientSpocName(clientLead.getClientSpocName())
                .clientSpocContact(clientLead.getClientSpocContact())
                .scopeDueDiligence(clientLead.getScopeDueDiligence())
                .scopeIprRetailer(clientLead.getScopeIprRetailer())
                .scopeIprSupplier(clientLead.getScopeIprSupplier())
                .scopeIprManufacturer(clientLead.getScopeIprManufacturer())
                .scopeOnlinePurchase(clientLead.getScopeOnlinePurchase())
                .scopeOfflinePurchase(clientLead.getScopeOfflinePurchase())
                .scopeCustomIds(clientLead.getScopeCustomIds())
                .entityName(clientLead.getEntityName())
                .suspectName(clientLead.getSuspectName())
                .contactNumbers(clientLead.getContactNumbers())
                .addressLine1(clientLead.getAddressLine1())
                .addressLine2(clientLead.getAddressLine2())
                .city(clientLead.getCity())
                .state(clientLead.getState())
                .pincode(clientLead.getPincode())
                .onlinePresences(onlinePresences)
                .productDetails(clientLead.getProductDetails())
                .photosProvided(clientLead.getPhotosProvided())
                .videoProvided(clientLead.getVideoProvided())
                .invoiceAvailable(clientLead.getInvoiceAvailable())
                .sourceNarrative(clientLead.getSourceNarrative())
                .verificationClientDiscussion(clientLead.getVerificationClientDiscussion())
                .verificationClientDiscussionNotes(clientLead.getVerificationClientDiscussionNotes())
                .verificationOsint(clientLead.getVerificationOsint())
                .verificationOsintNotes(clientLead.getVerificationOsintNotes())
                .verificationMarketplace(clientLead.getVerificationMarketplace())
                .verificationMarketplaceNotes(clientLead.getVerificationMarketplaceNotes())
                .verificationPretextCalling(clientLead.getVerificationPretextCalling())
                .verificationPretextCallingNotes(clientLead.getVerificationPretextCallingNotes())
                .verificationProductReview(clientLead.getVerificationProductReview())
                .verificationProductReviewNotes(clientLead.getVerificationProductReviewNotes())
                .obsIdentifiableTarget(clientLead.getObsIdentifiableTarget())
                .obsTraceability(clientLead.getObsTraceability())
                .obsProductVisibility(clientLead.getObsProductVisibility())
                .obsCounterfeitingIndications(clientLead.getObsCounterfeitingIndications())
                .obsEvidentiary_gaps(clientLead.getObsEvidentiary_gaps())
                .qaCompleteness(clientLead.getQaCompleteness())
                .qaAccuracy(clientLead.getQaAccuracy())
                .qaIndependentInvestigation(clientLead.getQaIndependentInvestigation())
                .qaPriorConfrontation(clientLead.getQaPriorConfrontation())
                .qaContaminationRisk(clientLead.getQaContaminationRisk())
                .assessmentOverall(clientLead.getAssessmentOverall())
                .assessmentRationale(clientLead.getAssessmentRationale())
                .recMarketSurvey(clientLead.getRecMarketSurvey())
                .recCovertInvestigation(clientLead.getRecCovertInvestigation())
                .recTestPurchase(clientLead.getRecTestPurchase())
                .recEnforcementAction(clientLead.getRecEnforcementAction())
                .recAdditionalInfo(clientLead.getRecAdditionalInfo())
                .recClosureHold(clientLead.getRecClosureHold())
                .remarks(clientLead.getRemarks())
                .customDisclaimer(clientLead.getCustomDisclaimer())
                .createdAt(clientLead.getCreatedAt())
                .updatedAt(clientLead.getUpdatedAt())
                .build();
    }
}
