package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.operation.prereport.dto.request.*;
import com.tbcpl.workforce.operation.prereport.dto.response.TrueBuddyLeadStepResponse;
import com.tbcpl.workforce.operation.prereport.entity.PreReport;
import com.tbcpl.workforce.operation.prereport.entity.PreReportTrueBuddyLead;
import com.tbcpl.workforce.operation.prereport.repository.PreReportRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportTrueBuddyLeadRepository;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PreReportTrueBuddyLeadService {

    private final PreReportTrueBuddyLeadRepository trueBuddyLeadRepository;
    private final PreReportRepository preReportRepository;  // ✅ ADDED
    private final PreReportService preReportService;

    public PreReportTrueBuddyLeadService(PreReportTrueBuddyLeadRepository trueBuddyLeadRepository,
                                         PreReportRepository preReportRepository,  // ✅ ADDED
                                         PreReportService preReportService) {
        this.trueBuddyLeadRepository = trueBuddyLeadRepository;
        this.preReportRepository = preReportRepository;  // ✅ ADDED
        this.preReportService = preReportService;
    }

    @Transactional
    public void initializeTrueBuddyLead(Long prereportId) {
        log.info("Initializing TrueBuddy lead for prereportId: {}", prereportId);

        PreReportTrueBuddyLead trueBuddyLead = PreReportTrueBuddyLead.builder()
                .prereportId(prereportId)
                .build();

        trueBuddyLeadRepository.save(trueBuddyLead);
    }

    @Transactional(readOnly = true)
    public TrueBuddyLeadStepResponse getTrueBuddyLeadByPrereportId(Long prereportId) {
        log.info("Fetching TrueBuddy lead data for prereportId: {}", prereportId);

        PreReportTrueBuddyLead trueBuddyLead = trueBuddyLeadRepository.findByPrereportId(prereportId)
                .orElseThrow(() -> new RuntimeException("TrueBuddy lead not found for prereport ID: " + prereportId));

        return mapToResponse(trueBuddyLead);
    }
    private void checkAndUpdateReportStatus(Long prereportId) {
        PreReport preReport = preReportRepository.findById(prereportId)
                .orElseThrow(() -> new RuntimeException("PreReport not found with ID: " + prereportId));

        // Check if report can be edited
        if (!preReport.canEdit()) {
            throw new RuntimeException("Report cannot be edited. Current status: " + preReport.getReportStatus());
        }

        // Auto-update from DRAFT to IN_PROGRESS when first step is filled
        if (preReport.getReportStatus() == ReportStatus.DRAFT) {
            preReport.setReportStatus(ReportStatus.IN_PROGRESS);
            preReportRepository.save(preReport);
        }
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep1(Long prereportId, TrueBuddyLeadStep1Request request) {
        log.info("Updating TrueBuddy lead step 1 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);


        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setDateInternalLeadGeneration(request.getDateInternalLeadGeneration());
        trueBuddyLead.setProductCategory(request.getProductCategory());
        trueBuddyLead.setInfringementType(request.getInfringementType());
        trueBuddyLead.setBroadGeography(request.getBroadGeography());
        trueBuddyLead.setClientSpocName(request.getClientSpocName());
        trueBuddyLead.setClientSpocDesignation(request.getClientSpocDesignation());
        trueBuddyLead.setNatureOfEntity(request.getNatureOfEntity());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 1);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep2(Long prereportId, TrueBuddyLeadStep2Request request) {
        log.info("Updating TrueBuddy lead step 2 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setScopeIprSupplier(request.getScopeIprSupplier());
        trueBuddyLead.setScopeIprManufacturer(request.getScopeIprManufacturer());
        trueBuddyLead.setScopeIprStockist(request.getScopeIprStockist());
        trueBuddyLead.setScopeMarketVerification(request.getScopeMarketVerification());
        trueBuddyLead.setScopeEtp(request.getScopeEtp());
        trueBuddyLead.setScopeEnforcement(request.getScopeEnforcement());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 2);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep3(Long prereportId, TrueBuddyLeadStep3Request request) {
        log.info("Updating TrueBuddy lead step 3 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setIntelNature(request.getIntelNature());
        trueBuddyLead.setSuspectedActivity(request.getSuspectedActivity());
        trueBuddyLead.setProductSegment(request.getProductSegment());
        trueBuddyLead.setSupplyChainStage(request.getSupplyChainStage());
        trueBuddyLead.setRepeatIntelligence(request.getRepeatIntelligence());
        trueBuddyLead.setMultiBrandRisk(request.getMultiBrandRisk());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 3);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep4(Long prereportId, TrueBuddyLeadStep4Request request) {
        log.info("Updating TrueBuddy lead step 4 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setVerificationIntelCorroboration(request.getVerificationIntelCorroboration());
        trueBuddyLead.setVerificationIntelCorroborationNotes(request.getVerificationIntelCorroborationNotes());
        trueBuddyLead.setVerificationOsint(request.getVerificationOsint());
        trueBuddyLead.setVerificationOsintNotes(request.getVerificationOsintNotes());
        trueBuddyLead.setVerificationPatternMapping(request.getVerificationPatternMapping());
        trueBuddyLead.setVerificationPatternMappingNotes(request.getVerificationPatternMappingNotes());
        trueBuddyLead.setVerificationJurisdiction(request.getVerificationJurisdiction());
        trueBuddyLead.setVerificationJurisdictionNotes(request.getVerificationJurisdictionNotes());
        trueBuddyLead.setVerificationRiskAssessment(request.getVerificationRiskAssessment());
        trueBuddyLead.setVerificationRiskAssessmentNotes(request.getVerificationRiskAssessmentNotes());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 4);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep5(Long prereportId, TrueBuddyLeadStep5Request request) {
        log.info("Updating TrueBuddy lead step 5 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setObsOperationScale(request.getObsOperationScale());
        trueBuddyLead.setObsCounterfeitLikelihood(request.getObsCounterfeitLikelihood());
        trueBuddyLead.setObsBrandExposure(request.getObsBrandExposure());
        trueBuddyLead.setObsEnforcementSensitivity(request.getObsEnforcementSensitivity());
        trueBuddyLead.setObsLeakageRisk(request.getObsLeakageRisk());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 5);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep6(Long prereportId, TrueBuddyLeadStep6Request request) {
        log.info("Updating TrueBuddy lead step 6 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setRiskSourceReliability(request.getRiskSourceReliability());
        trueBuddyLead.setRiskClientConflict(request.getRiskClientConflict());
        trueBuddyLead.setRiskImmediateAction(request.getRiskImmediateAction());
        trueBuddyLead.setRiskControlledValidation(request.getRiskControlledValidation());
        trueBuddyLead.setRiskPrematureDisclosure(request.getRiskPrematureDisclosure());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 6);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep7(Long prereportId, TrueBuddyLeadStep7Request request) {
        log.info("Updating TrueBuddy lead step 7 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setAssessmentOverall(request.getAssessmentOverall());
        trueBuddyLead.setAssessmentRationale(request.getAssessmentRationale());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 7);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep8(Long prereportId, TrueBuddyLeadStep8Request request) {
        log.info("Updating TrueBuddy lead step 8 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setRecCovertValidation(request.getRecCovertValidation());
        trueBuddyLead.setRecEtp(request.getRecEtp());
        trueBuddyLead.setRecMarketReconnaissance(request.getRecMarketReconnaissance());
        trueBuddyLead.setRecEnforcementDeferred(request.getRecEnforcementDeferred());
        trueBuddyLead.setRecContinuedMonitoring(request.getRecContinuedMonitoring());
        trueBuddyLead.setRecClientSegregation(request.getRecClientSegregation());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 8);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep9(Long prereportId, TrueBuddyLeadStep9Request request) {
        log.info("Updating TrueBuddy lead step 9 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setConfidentialityNote(request.getConfidentialityNote());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 9);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep10(Long prereportId, TrueBuddyLeadStep10Request request) {
        log.info("Updating TrueBuddy lead step 10 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setRemarks(request.getRemarks());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 10);

        return mapToResponse(trueBuddyLead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep11(Long prereportId, TrueBuddyLeadStep11Request request) {
        log.info("Updating TrueBuddy lead step 11 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);

        PreReportTrueBuddyLead trueBuddyLead = getTrueBuddyLeadEntity(prereportId);

        trueBuddyLead.setCustomDisclaimer(request.getCustomDisclaimer());

        trueBuddyLeadRepository.save(trueBuddyLead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 11);

        return mapToResponse(trueBuddyLead);
    }

    private PreReportTrueBuddyLead getTrueBuddyLeadEntity(Long prereportId) {
        return trueBuddyLeadRepository.findByPrereportId(prereportId)
                .orElseThrow(() -> new RuntimeException("TrueBuddy lead not found for prereport ID: " + prereportId));
    }

    // ✅ FIXED: Properly fetch reportId from PreReport table
    private String getReportIdFromPrereportId(Long prereportId) {
        PreReport preReport = preReportRepository.findById(prereportId)
                .orElseThrow(() -> new RuntimeException("PreReport not found with ID: " + prereportId));
        return preReport.getReportId();
    }

    private TrueBuddyLeadStepResponse mapToResponse(PreReportTrueBuddyLead trueBuddyLead) {
        return TrueBuddyLeadStepResponse.builder()
                .id(trueBuddyLead.getId())
                .prereportId(trueBuddyLead.getPrereportId())
                .dateInternalLeadGeneration(trueBuddyLead.getDateInternalLeadGeneration())
                .productCategory(trueBuddyLead.getProductCategory())
                .infringementType(trueBuddyLead.getInfringementType())
                .broadGeography(trueBuddyLead.getBroadGeography())
                .clientSpocName(trueBuddyLead.getClientSpocName())
                .clientSpocDesignation(trueBuddyLead.getClientSpocDesignation())
                .natureOfEntity(trueBuddyLead.getNatureOfEntity())
                .scopeIprSupplier(trueBuddyLead.getScopeIprSupplier())
                .scopeIprManufacturer(trueBuddyLead.getScopeIprManufacturer())
                .scopeIprStockist(trueBuddyLead.getScopeIprStockist())
                .scopeMarketVerification(trueBuddyLead.getScopeMarketVerification())
                .scopeEtp(trueBuddyLead.getScopeEtp())
                .scopeEnforcement(trueBuddyLead.getScopeEnforcement())
                .intelNature(trueBuddyLead.getIntelNature())
                .suspectedActivity(trueBuddyLead.getSuspectedActivity())
                .productSegment(trueBuddyLead.getProductSegment())
                .supplyChainStage(trueBuddyLead.getSupplyChainStage())
                .repeatIntelligence(trueBuddyLead.getRepeatIntelligence())
                .multiBrandRisk(trueBuddyLead.getMultiBrandRisk())
                .verificationIntelCorroboration(trueBuddyLead.getVerificationIntelCorroboration())
                .verificationIntelCorroborationNotes(trueBuddyLead.getVerificationIntelCorroborationNotes())
                .verificationOsint(trueBuddyLead.getVerificationOsint())
                .verificationOsintNotes(trueBuddyLead.getVerificationOsintNotes())
                .verificationPatternMapping(trueBuddyLead.getVerificationPatternMapping())
                .verificationPatternMappingNotes(trueBuddyLead.getVerificationPatternMappingNotes())
                .verificationJurisdiction(trueBuddyLead.getVerificationJurisdiction())
                .verificationJurisdictionNotes(trueBuddyLead.getVerificationJurisdictionNotes())
                .verificationRiskAssessment(trueBuddyLead.getVerificationRiskAssessment())
                .verificationRiskAssessmentNotes(trueBuddyLead.getVerificationRiskAssessmentNotes())
                .obsOperationScale(trueBuddyLead.getObsOperationScale())
                .obsCounterfeitLikelihood(trueBuddyLead.getObsCounterfeitLikelihood())
                .obsBrandExposure(trueBuddyLead.getObsBrandExposure())
                .obsEnforcementSensitivity(trueBuddyLead.getObsEnforcementSensitivity())
                .obsLeakageRisk(trueBuddyLead.getObsLeakageRisk())
                .riskSourceReliability(trueBuddyLead.getRiskSourceReliability())
                .riskClientConflict(trueBuddyLead.getRiskClientConflict())
                .riskImmediateAction(trueBuddyLead.getRiskImmediateAction())
                .riskControlledValidation(trueBuddyLead.getRiskControlledValidation())
                .riskPrematureDisclosure(trueBuddyLead.getRiskPrematureDisclosure())
                .assessmentOverall(trueBuddyLead.getAssessmentOverall())
                .assessmentRationale(trueBuddyLead.getAssessmentRationale())
                .recCovertValidation(trueBuddyLead.getRecCovertValidation())
                .recEtp(trueBuddyLead.getRecEtp())
                .recMarketReconnaissance(trueBuddyLead.getRecMarketReconnaissance())
                .recEnforcementDeferred(trueBuddyLead.getRecEnforcementDeferred())
                .recContinuedMonitoring(trueBuddyLead.getRecContinuedMonitoring())
                .recClientSegregation(trueBuddyLead.getRecClientSegregation())
                .confidentialityNote(trueBuddyLead.getConfidentialityNote())
                .remarks(trueBuddyLead.getRemarks())
                .customDisclaimer(trueBuddyLead.getCustomDisclaimer())
                .createdAt(trueBuddyLead.getCreatedAt())
                .updatedAt(trueBuddyLead.getUpdatedAt())
                .build();
    }
}
