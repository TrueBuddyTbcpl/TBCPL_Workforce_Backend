package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.operation.prereport.dto.request.*;
import com.tbcpl.workforce.operation.prereport.dto.response.TrueBuddyLeadStepResponse;
import com.tbcpl.workforce.operation.prereport.entity.PreReport;
import com.tbcpl.workforce.operation.prereport.entity.PreReportTrueBuddyLead;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import com.tbcpl.workforce.operation.prereport.repository.PreReportRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportTrueBuddyLeadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PreReportTrueBuddyLeadService {

    private final PreReportTrueBuddyLeadRepository trueBuddyLeadRepository;
    private final PreReportRepository preReportRepository;
    private final PreReportService preReportService;

    public PreReportTrueBuddyLeadService(
            PreReportTrueBuddyLeadRepository trueBuddyLeadRepository,
            PreReportRepository preReportRepository,
            @Lazy PreReportService preReportService) {
        this.trueBuddyLeadRepository = trueBuddyLeadRepository;
        this.preReportRepository = preReportRepository;
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
        return mapToResponse(getTrueBuddyLeadEntity(prereportId));
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep1(Long prereportId, TrueBuddyLeadStep1Request request) {
        log.info("Updating TrueBuddy lead step 1 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setDateInternalLeadGeneration(request.getDateInternalLeadGeneration());
        lead.setProductCategory(request.getProductCategory());
        lead.setProductCategoryCustomText(request.getProductCategoryCustomText());      // ← CHANGED
        lead.setInfringementType(request.getInfringementType());
        lead.setInfringementTypeCustomText(request.getInfringementTypeCustomText());    // ← CHANGED
        lead.setBroadGeography(request.getBroadGeography());
        lead.setReasonOfSuspicion(request.getReasonOfSuspicion());
        lead.setReasonOfSuspicionCustomText(request.getReasonOfSuspicionCustomText());  // ← CHANGED
        lead.setExpectedSeizure(request.getExpectedSeizure());
        lead.setNatureOfEntity(request.getNatureOfEntity());
        lead.setNatureOfEntityCustomText(request.getNatureOfEntityCustomText());        // ← CHANGED

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 1);
        preReportService.markStepAsCOMPLETED(prereportId, 1);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep2(Long prereportId, TrueBuddyLeadStep2Request request) {
        log.info("Updating TrueBuddy lead step 2 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setScopeIprSupplier(request.getScopeIprSupplier());
        lead.setScopeIprManufacturer(request.getScopeIprManufacturer());
        lead.setScopeIprStockist(request.getScopeIprStockist());
        lead.setScopeMarketVerification(request.getScopeMarketVerification());
        lead.setScopeEtp(request.getScopeEtp());
        lead.setScopeEnforcement(request.getScopeEnforcement());
        lead.setScopeCustomIds(request.getScopeCustomIds());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 2);
        preReportService.markStepAsCOMPLETED(prereportId, 2);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep3(Long prereportId, TrueBuddyLeadStep3Request request) {
        log.info("Updating TrueBuddy lead step 3 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setIntelNature(request.getIntelNature());
        lead.setIntelNatureCustomText(request.getIntelNatureCustomText());              // ← CHANGED
        lead.setSuspectedActivity(request.getSuspectedActivity());
        lead.setSuspectedActivityCustomText(request.getSuspectedActivityCustomText()); // ← CHANGED
        lead.setProductSegment(request.getProductSegment());
        lead.setProductSegmentCustomText(request.getProductSegmentCustomText());       // ← CHANGED
        lead.setRepeatIntelligence(request.getRepeatIntelligence());
        lead.setMultiBrandRisk(request.getMultiBrandRisk());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 3);
        preReportService.markStepAsCOMPLETED(prereportId, 3);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep4(Long prereportId, TrueBuddyLeadStep4Request request) {
        log.info("Updating TrueBuddy lead step 4 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setVerificationIntelCorroboration(request.getVerificationIntelCorroboration());
        lead.setVerificationIntelCorroborationNotes(request.getVerificationIntelCorroborationNotes());
        lead.setVerificationOsint(request.getVerificationOsint());
        lead.setVerificationOsintNotes(request.getVerificationOsintNotes());
        lead.setVerificationPatternMapping(request.getVerificationPatternMapping());
        lead.setVerificationPatternMappingNotes(request.getVerificationPatternMappingNotes());
        lead.setVerificationJurisdiction(request.getVerificationJurisdiction());
        lead.setVerificationJurisdictionNotes(request.getVerificationJurisdictionNotes());
        lead.setVerificationRiskAssessment(request.getVerificationRiskAssessment());
        lead.setVerificationRiskAssessmentNotes(request.getVerificationRiskAssessmentNotes());
        lead.setVerificationCustomData(request.getVerificationCustomData());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 4);
        preReportService.markStepAsCOMPLETED(prereportId, 4);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep5(Long prereportId, TrueBuddyLeadStep5Request request) {
        log.info("Updating TrueBuddy lead step 5 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setObsOperationScale(request.getObsOperationScale());
        lead.setObsCounterfeitLikelihood(request.getObsCounterfeitLikelihood());
        lead.setObsBrandExposure(request.getObsBrandExposure());
        lead.setObsBrandExposureCustomText(request.getObsBrandExposureCustomText());   // ← CHANGED
        lead.setObsEnforcementSensitivity(request.getObsEnforcementSensitivity());
        lead.setObservationsCustomData(request.getObservationsCustomData());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 5);
        preReportService.markStepAsCOMPLETED(prereportId, 5);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep6(Long prereportId, TrueBuddyLeadStep6Request request) {
        log.info("Updating TrueBuddy lead step 6 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setRiskSourceReliability(request.getRiskSourceReliability());
        lead.setRiskClientConflict(request.getRiskClientConflict());
        lead.setRiskImmediateAction(request.getRiskImmediateAction());
        lead.setRiskControlledValidation(request.getRiskControlledValidation());
        lead.setRiskCustomData(request.getRiskCustomData());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 6);
        preReportService.markStepAsCOMPLETED(prereportId, 6);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep7(Long prereportId, TrueBuddyLeadStep7Request request) {
        log.info("Updating TrueBuddy lead step 7 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setAssessmentOverall(request.getAssessmentOverall());
        lead.setAssessmentRationale(request.getAssessmentRationale());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 7);
        preReportService.markStepAsCOMPLETED(prereportId, 7);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep8(Long prereportId, TrueBuddyLeadStep8Request request) {
        log.info("Updating TrueBuddy lead step 8 for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setRecCovertValidation(request.getRecCovertValidation());
        lead.setRecEtp(request.getRecEtp());
        lead.setRecMarketReconnaissance(request.getRecMarketReconnaissance());
        lead.setRecEnforcementDeferred(request.getRecEnforcementDeferred());
        lead.setRecContinuedMonitoring(request.getRecContinuedMonitoring());
        lead.setRecClientSegregation(request.getRecClientSegregation());
        lead.setRecCustomIds(request.getRecCustomIds());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 8);
        preReportService.markStepAsCOMPLETED(prereportId, 8);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep9(Long prereportId, TrueBuddyLeadStep9Request request) {
        log.info("Updating TrueBuddy lead step 9 (Remarks) for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setRemarks(request.getRemarks());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 9);
        preReportService.markStepAsCOMPLETED(prereportId, 9);
        return mapToResponse(lead);
    }

    @Transactional
    public TrueBuddyLeadStepResponse updateStep10(Long prereportId, TrueBuddyLeadStep10Request request) {
        log.info("Updating TrueBuddy lead step 10 (Disclaimer) for prereportId: {}", prereportId);
        checkAndUpdateReportStatus(prereportId);
        PreReportTrueBuddyLead lead = getTrueBuddyLeadEntity(prereportId);

        lead.setCustomDisclaimer(request.getCustomDisclaimer());

        trueBuddyLeadRepository.save(lead);
        preReportService.updateCurrentStep(getReportIdFromPrereportId(prereportId), 10);
        preReportService.markStepAsCOMPLETED(prereportId, 10);
        return mapToResponse(lead);
    }

    private void checkAndUpdateReportStatus(Long prereportId) {
        PreReport preReport = preReportRepository.findById(prereportId)
                .orElseThrow(() -> new RuntimeException("PreReport not found with ID: " + prereportId));
        if (!preReport.canEdit()) {
            throw new RuntimeException("Report cannot be edited. Current status: " + preReport.getReportStatus());
        }
        if (preReport.getReportStatus() == ReportStatus.DRAFT) {
            preReport.setReportStatus(ReportStatus.IN_PROGRESS);
            preReportRepository.save(preReport);
        }
    }

    private PreReportTrueBuddyLead getTrueBuddyLeadEntity(Long prereportId) {
        return trueBuddyLeadRepository.findByPrereportId(prereportId)
                .orElseThrow(() -> new RuntimeException("TrueBuddy lead not found for prereport ID: " + prereportId));
    }

    private String getReportIdFromPrereportId(Long prereportId) {
        return preReportRepository.findById(prereportId)
                .orElseThrow(() -> new RuntimeException("PreReport not found with ID: " + prereportId))
                .getReportId();
    }

    private TrueBuddyLeadStepResponse mapToResponse(PreReportTrueBuddyLead lead) {
        return TrueBuddyLeadStepResponse.builder()
                .id(lead.getId())
                .prereportId(lead.getPrereportId())
                // Step 1
                .dateInternalLeadGeneration(lead.getDateInternalLeadGeneration())
                .productCategory(lead.getProductCategory())
                .productCategoryCustomText(lead.getProductCategoryCustomText())         // ← CHANGED
                .infringementType(lead.getInfringementType())
                .infringementTypeCustomText(lead.getInfringementTypeCustomText())       // ← CHANGED
                .broadGeography(lead.getBroadGeography())
                .reasonOfSuspicion(lead.getReasonOfSuspicion())
                .reasonOfSuspicionCustomText(lead.getReasonOfSuspicionCustomText())     // ← CHANGED
                .expectedSeizure(lead.getExpectedSeizure())
                .natureOfEntity(lead.getNatureOfEntity())
                .natureOfEntityCustomText(lead.getNatureOfEntityCustomText())           // ← CHANGED
                // Step 2
                .scopeIprSupplier(lead.getScopeIprSupplier())
                .scopeIprManufacturer(lead.getScopeIprManufacturer())
                .scopeIprStockist(lead.getScopeIprStockist())
                .scopeMarketVerification(lead.getScopeMarketVerification())
                .scopeEtp(lead.getScopeEtp())
                .scopeEnforcement(lead.getScopeEnforcement())
                .scopeCustomIds(lead.getScopeCustomIds())
                // Step 3
                .intelNature(lead.getIntelNature())
                .intelNatureCustomText(lead.getIntelNatureCustomText())                 // ← CHANGED
                .suspectedActivity(lead.getSuspectedActivity())
                .suspectedActivityCustomText(lead.getSuspectedActivityCustomText())    // ← CHANGED
                .productSegment(lead.getProductSegment())
                .productSegmentCustomText(lead.getProductSegmentCustomText())          // ← CHANGED
                .repeatIntelligence(lead.getRepeatIntelligence())
                .multiBrandRisk(lead.getMultiBrandRisk())
                // Step 4
                .verificationIntelCorroboration(lead.getVerificationIntelCorroboration())
                .verificationIntelCorroborationNotes(lead.getVerificationIntelCorroborationNotes())
                .verificationOsint(lead.getVerificationOsint())
                .verificationOsintNotes(lead.getVerificationOsintNotes())
                .verificationPatternMapping(lead.getVerificationPatternMapping())
                .verificationPatternMappingNotes(lead.getVerificationPatternMappingNotes())
                .verificationJurisdiction(lead.getVerificationJurisdiction())
                .verificationJurisdictionNotes(lead.getVerificationJurisdictionNotes())
                .verificationRiskAssessment(lead.getVerificationRiskAssessment())
                .verificationRiskAssessmentNotes(lead.getVerificationRiskAssessmentNotes())
                .verificationCustomData(lead.getVerificationCustomData())
                // Step 5
                .obsOperationScale(lead.getObsOperationScale())
                .obsCounterfeitLikelihood(lead.getObsCounterfeitLikelihood())
                .obsBrandExposure(lead.getObsBrandExposure())
                .obsBrandExposureCustomText(lead.getObsBrandExposureCustomText())      // ← CHANGED
                .obsEnforcementSensitivity(lead.getObsEnforcementSensitivity())
                .observationsCustomData(lead.getObservationsCustomData())
                // Step 6
                .riskSourceReliability(lead.getRiskSourceReliability())
                .riskClientConflict(lead.getRiskClientConflict())
                .riskImmediateAction(lead.getRiskImmediateAction())
                .riskControlledValidation(lead.getRiskControlledValidation())
                .riskCustomData(lead.getRiskCustomData())
                // Step 7
                .assessmentOverall(lead.getAssessmentOverall())
                .assessmentRationale(lead.getAssessmentRationale())
                // Step 8
                .recCovertValidation(lead.getRecCovertValidation())
                .recEtp(lead.getRecEtp())
                .recMarketReconnaissance(lead.getRecMarketReconnaissance())
                .recEnforcementDeferred(lead.getRecEnforcementDeferred())
                .recContinuedMonitoring(lead.getRecContinuedMonitoring())
                .recClientSegregation(lead.getRecClientSegregation())
                .recCustomIds(lead.getRecCustomIds())
                // Step 9
                .remarks(lead.getRemarks())
                // Step 10
                .customDisclaimer(lead.getCustomDisclaimer())
                // Audit
                .createdAt(lead.getCreatedAt())
                .updatedAt(lead.getUpdatedAt())
                .build();
    }
}