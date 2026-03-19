package com.tbcpl.workforce.operation.cases.service;

import com.tbcpl.workforce.admin.entity.ClientProduct;
import com.tbcpl.workforce.admin.repository.ClientProductRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;

import com.tbcpl.workforce.common.util.S3Service;
import com.tbcpl.workforce.operation.cases.dto.request.AddCaseUpdateRequest;
import com.tbcpl.workforce.operation.cases.dto.request.CreateCaseRequest;
import com.tbcpl.workforce.operation.cases.dto.request.LinkProfileRequest;
import com.tbcpl.workforce.operation.cases.dto.response.*;
import com.tbcpl.workforce.operation.cases.entity.*;
import com.tbcpl.workforce.operation.cases.repository.CaseDocumentRepository;
import com.tbcpl.workforce.operation.cases.repository.CaseLinkedProfileRepository;
import com.tbcpl.workforce.operation.cases.repository.CaseRepository;
import com.tbcpl.workforce.operation.prereport.entity.PreReport;
import com.tbcpl.workforce.operation.prereport.entity.PreReportOnlinePresence;
import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import com.tbcpl.workforce.operation.prereport.repository.PreReportClientLeadRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportOnlinePresenceRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportTrueBuddyLeadRepository;
import com.tbcpl.workforce.operation.cases.repository.CaseUpdateRepository;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.operation.prereport.service.PreReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



@Service
@Slf4j
public class CaseServiceImpl implements CaseService {

    private final CaseRepository caseRepository;
    private final PreReportRepository preReportRepository;
    private final PreReportClientLeadRepository clientLeadRepository;
    private final PreReportTrueBuddyLeadRepository trueBuddyLeadRepository;
    private final PreReportOnlinePresenceRepository onlinePresenceRepository;
    private final ClientProductRepository clientProductRepository;
    private final CaseUpdateRepository caseUpdateRepository;
    private final CaseDocumentRepository caseDocumentRepository;
    private final S3Service s3Service;
    private final EmployeeRepository employeeRepository;
    private final PreReportService preReportService;
    private final CaseLinkedProfileRepository linkedProfileRepository;


    public CaseServiceImpl(
            CaseRepository caseRepository,
            PreReportRepository preReportRepository,
            PreReportClientLeadRepository clientLeadRepository,
            PreReportTrueBuddyLeadRepository trueBuddyLeadRepository,
            PreReportOnlinePresenceRepository onlinePresenceRepository,
            ClientProductRepository clientProductRepository,
            CaseUpdateRepository caseUpdateRepository,
            CaseDocumentRepository caseDocumentRepository,  // ✅ ADD
            S3Service s3Service,
            EmployeeRepository employeeRepository,
            PreReportService preReportService,
            CaseLinkedProfileRepository linkedProfileRepository
    ) {
        this.caseRepository = caseRepository;
        this.preReportRepository = preReportRepository;
        this.clientLeadRepository = clientLeadRepository;
        this.trueBuddyLeadRepository = trueBuddyLeadRepository;
        this.onlinePresenceRepository = onlinePresenceRepository;
        this.clientProductRepository = clientProductRepository;
        this.caseUpdateRepository = caseUpdateRepository;
        this.caseDocumentRepository   = caseDocumentRepository;  // ✅ ADD
        this.s3Service        = s3Service;
        this.employeeRepository       = employeeRepository;
        this.preReportService = preReportService;
        this.linkedProfileRepository = linkedProfileRepository;
    }

    // ─────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public CaseResponse createCaseFromPreReport(Long prereportId, CreateCaseRequest request, String createdBy) {
        log.info("Creating case from prereport ID: {} by user: {}", prereportId, createdBy);

        PreReport preReport = preReportRepository.findById(prereportId)
                .filter(p -> !p.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException("PreReport not found with ID: " + prereportId));

        if (preReport.getReportStatus() != ReportStatus.READY_FOR_CREATE_CASE) {
            throw new IllegalStateException("PreReport is not in READY_FOR_CREATE_CASE status. Current status: "
                    + preReport.getReportStatus());
        }

        if (caseRepository.existsByPrereportIdAndIsDeletedFalse(prereportId)) {
            throw new IllegalStateException("A case already exists for prereport ID: " + prereportId);
        }

        if (request.getAssignedEmployees() == null || request.getAssignedEmployees().isEmpty()) {
            throw new IllegalArgumentException("At least one Operations employee must be assigned to the case");
        }

        List<String> validEmpIds = employeeRepository
                .findActiveEmployeesByDepartmentName("Operation")
                .stream()
                .map(Employee::getEmpId)
                .toList();

        for (String empId : request.getAssignedEmployees()) {
            if (!validEmpIds.contains(empId)) {
                throw new IllegalArgumentException(
                        "Employee '" + empId + "' is not in Operations department or is inactive"
                );
            }
        }



        String caseNumber = generateCaseNumber();
        String clientName = preReport.getClient().getClientName();
        Long clientId = preReport.getClient().getClientId();
        String clientProducts = resolveProductNames(preReport.getProductIds());
        String caseTitle = buildCaseTitle(preReport, caseNumber);

        Case newCase = Case.builder()
                .caseNumber(caseNumber)
                .prereportId(prereportId)
                .prereportReportId(preReport.getReportId())
                .leadType(preReport.getLeadType().name())
                .caseTitle(caseTitle)
                .priority(request.getPriority())
                .status("open")
                .caseType(request.getCaseType())
                .dateOpened(LocalDate.now())
                .clientId(clientId)
                .clientName(clientName)
                .clientProduct(clientProducts)
                .clientEmail(request.getClientEmail())
                .assignedEmployees(request.getAssignedEmployees() != null
                        ? String.join(",", request.getAssignedEmployees())
                        : null)
                .estimatedCompletionDate(request.getEstimatedCompletionDate())
                .createdBy(createdBy)
                .build();

        if (preReport.getLeadType() == LeadType.CLIENT_LEAD) {
            mapClientLeadData(newCase, prereportId);
        } else {
            mapTrueBuddyLeadData(newCase, prereportId);
        }

        CaseUpdate initialUpdate = CaseUpdate.builder()
                .caseEntity(newCase)
                .updateDate(LocalDateTime.now())
                .updatedBy(createdBy)
                .description("Case created from PreReport: " + preReport.getReportId())
                .build();

        newCase.getUpdates().add(initialUpdate);

        Case savedCase = caseRepository.save(newCase);
        log.info("Case created successfully: {}", savedCase.getCaseNumber());
        preReportService.markCaseGenerated(
                prereportId,
                savedCase.getCaseNumber(),
                savedCase.getId()
        );

        return mapToCaseResponse(savedCase);
    }

    // ─────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────

    @Transactional
    public void addUpdate(Long caseId, AddCaseUpdateRequest request, String updatedBy) {
        Case caseEntity = caseRepository.findById(caseId)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        CaseUpdate update = CaseUpdate.builder()
                .caseEntity(caseEntity)
                .updateDate(LocalDateTime.now())
                .updatedBy(updatedBy)
                .description(request.getDescription())
                .build();

        caseUpdateRepository.save(update);
        log.info("Update added to case {} by {}", caseId, updatedBy);
    }


    @Override
    @Transactional(readOnly = true)
    public CaseResponse getCaseById(Long caseId) {
        Case caseEntity = caseRepository.findById(caseId)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Case not found with ID: " + caseId));
        return mapToCaseResponse(caseEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public CaseResponse getCaseByCaseNumber(String caseNumber) {
        Case caseEntity = caseRepository.findByCaseNumberAndIsDeletedFalse(caseNumber)
                .orElseThrow(() -> new EntityNotFoundException("Case not found with number: " + caseNumber));
        return mapToCaseResponse(caseEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseListItemResponse> getAllCases(Pageable pageable) {
        return caseRepository.findAllActiveCases(pageable)
                .map(this::mapToCaseListItemResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseListItemResponse> getCasesByClientId(Long clientId, Pageable pageable) {
        return caseRepository.findByClientId(clientId, pageable)
                .map(this::mapToCaseListItemResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CaseListItemResponse> getCasesByStatus(String status, Pageable pageable) {
        return caseRepository.findByStatus(status, pageable)
                .map(this::mapToCaseListItemResponse);
    }

    // ─────────────────────────────────────────────────────────────────────
// DOCUMENT METHODS
// ─────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<CaseDocumentResponse> getCaseDocuments(Long caseId) {
        validateCaseExists(caseId);
        return caseDocumentRepository.findByCaseId(caseId)
                .stream()
                .map(this::mapToDocumentResponse)
                .toList();
    }

    @Override
    @Transactional
    public CaseDocumentResponse uploadDocument(Long caseId, MultipartFile file, String uploadedBy) {
        Case caseEntity = caseRepository.findById(caseId)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException("Case not found: " + caseId));

        try {
            Map<String, String> uploadResult = s3Service.uploadFile(
                    file, "cases/" + caseEntity.getCaseNumber()
            );

            CaseDocument document = CaseDocument.builder()
                    .caseEntity(caseEntity)
                    .fileName(uploadResult.get("file_name"))
                    .originalName(file.getOriginalFilename())
                    .fileUrl(uploadResult.get("url"))
                    .publicId(uploadResult.get("key"))
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedBy(uploadedBy)
                    .build();

            CaseDocument saved = caseDocumentRepository.save(document);
            log.info("Document uploaded for case {} by {}", caseId, uploadedBy);
            return mapToDocumentResponse(saved);

        } catch (IOException e) {
            log.error("Failed to upload document for case {}: {}", caseId, e.getMessage());
            throw new RuntimeException("Failed to upload document: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deleteDocument(Long caseId, Long documentId, String requestedBy) {
        CaseDocument document = caseDocumentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found: " + documentId));

        if (!document.getCaseEntity().getId().equals(caseId)) {
            throw new IllegalArgumentException("Document does not belong to this case");
        }

        s3Service.deleteFile(document.getPublicId());
        caseDocumentRepository.delete(document);
        log.info("Document {} deleted from case {} by {}", documentId, caseId, requestedBy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperationsEmployeeResponse> getOperationsEmployees() {
        return employeeRepository.findActiveEmployeesByDepartmentName("Operation")
                .stream()
                .map(emp -> OperationsEmployeeResponse.builder()
                        .id(emp.getId())
                        .empId(emp.getEmpId())
                        .fullName(emp.getFullName())
                        .email(emp.getEmail())
                        .roleName(emp.getRole().getRoleName() != null
                                ? emp.getRole().getRoleName()
                                : "N/A")
                        .departmentName(emp.getDepartment().getDepartmentName())
                        .build())
                .toList();
    }

    // Add CaseLinkedProfileReposito

    // ── Link Profile ───────────────────────────────────────────────────────────
    @Override
    @Transactional
    public LinkedProfileResponse linkProfile(Long caseId, LinkProfileRequest request, String linkedBy) {
        Case caseEntity = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + caseId));

        if (linkedProfileRepository.existsByCaseEntity_IdAndProfileId(caseId, request.profileId())) {
            throw new IllegalStateException("Profile already linked to this case");
        }

        CaseLinkedProfile link = CaseLinkedProfile.builder()
                .caseEntity(caseEntity)
                .profileId(request.profileId())
                .profileNumber(request.profileNumber())
                .profileName(request.profileName())
                .linkedBy(linkedBy)
                .build();

        CaseLinkedProfile saved = linkedProfileRepository.save(link);
        log.info("Profile {} linked to case {} by {}", request.profileNumber(), caseId, linkedBy);

        return toLinkedProfileResponse(saved);
    }

    // ── Unlink Profile ─────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void unlinkProfile(Long caseId, Long profileId, String requestedBy) {
        if (!linkedProfileRepository.existsByCaseEntity_IdAndProfileId(caseId, profileId)) {
            throw new ResourceNotFoundException("Link not found");
        }
        linkedProfileRepository.deleteByCaseEntity_IdAndProfileId(caseId, profileId);
        log.info("Profile {} unlinked from case {} by {}", profileId, caseId, requestedBy);
    }

    // ── Get Linked Profiles ────────────────────────────────────────────────────
    @Override
    public List<LinkedProfileResponse> getLinkedProfiles(Long caseId) {
        return linkedProfileRepository.findByCaseEntity_Id(caseId)
                .stream()
                .map(this::toLinkedProfileResponse)
                .toList();
    }

    // ── Count Cases for Profile ────────────────────────────────────────────────
    @Override
    public long countLinkedCasesForProfile(Long profileId) {
        return linkedProfileRepository.countByProfileId(profileId);
    }

    // ── Mapper ─────────────────────────────────────────────────────────────────
    private LinkedProfileResponse toLinkedProfileResponse(CaseLinkedProfile lp) {
        return new LinkedProfileResponse(
                lp.getId(),
                lp.getProfileId(),
                lp.getProfileNumber(),
                lp.getProfileName(),
                lp.getLinkedBy(),
                lp.getLinkedAt()
        );
    }



    // ── Private helper ─────────────────────────────────────────────────────
    private CaseDocumentResponse mapToDocumentResponse(CaseDocument doc) {
        return CaseDocumentResponse.builder()
                .id(doc.getId())
                .fileName(doc.getFileName())
                .originalName(doc.getOriginalName())
                .fileUrl(doc.getFileUrl())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .uploadedBy(doc.getUploadedBy())
                .uploadedAt(doc.getUploadedAt())
                .build();
    }

    private void validateCaseExists(Long caseId) {
        if (!caseRepository.existsById(caseId)) {
            throw new EntityNotFoundException("Case not found: " + caseId);
        }
    }


    // ─────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────

    private void mapClientLeadData(Case newCase, Long prereportId) {
        clientLeadRepository.findByPrereportId(prereportId).ifPresent(cl -> {
            newCase.setScopeDueDiligence(cl.getScopeDueDiligence());
            newCase.setScopeIprRetailer(cl.getScopeIprRetailer());
            newCase.setScopeIprSupplier(cl.getScopeIprSupplier());
            newCase.setScopeIprManufacturer(cl.getScopeIprManufacturer());
            newCase.setScopeOnlinePurchase(cl.getScopeOnlinePurchase());
            newCase.setScopeOfflinePurchase(cl.getScopeOfflinePurchase());
            newCase.setEntityName(cl.getEntityName());
            newCase.setSuspectName(cl.getSuspectName());
            newCase.setContactNumbers(cl.getContactNumbers());
            newCase.setAddressLine1(cl.getAddressLine1());
            newCase.setAddressLine2(cl.getAddressLine2());
            newCase.setCity(cl.getCity());
            newCase.setState(cl.getState());
            newCase.setPincode(cl.getPincode());
            newCase.setProductDetails(cl.getProductDetails());
            newCase.setPhotosProvided(cl.getPhotosProvided() != null
                    ? cl.getPhotosProvided().name() : null);
            newCase.setVideoProvided(cl.getVideoProvided() != null
                    ? cl.getVideoProvided().name() : null);
            newCase.setInvoiceAvailable(cl.getInvoiceAvailable() != null
                    ? cl.getInvoiceAvailable().name() : null);
            newCase.setSourceNarrative(cl.getSourceNarrative());
            newCase.setVerificationClientDiscussion(cl.getVerificationClientDiscussion() != null
                    ? cl.getVerificationClientDiscussion().name() : null);
            newCase.setVerificationClientDiscussionNotes(cl.getVerificationClientDiscussionNotes());
            newCase.setVerificationOsint(cl.getVerificationOsint() != null
                    ? cl.getVerificationOsint().name() : null);
            newCase.setVerificationOsintNotes(cl.getVerificationOsintNotes());
            newCase.setVerificationMarketplace(cl.getVerificationMarketplace() != null
                    ? cl.getVerificationMarketplace().name() : null);
            newCase.setVerificationMarketplaceNotes(cl.getVerificationMarketplaceNotes());
            newCase.setVerificationPretextCalling(cl.getVerificationPretextCalling() != null
                    ? cl.getVerificationPretextCalling().name() : null);
            newCase.setVerificationPretextCallingNotes(cl.getVerificationPretextCallingNotes());
            newCase.setVerificationProductReview(cl.getVerificationProductReview() != null
                    ? cl.getVerificationProductReview().name() : null);
            newCase.setVerificationProductReviewNotes(cl.getVerificationProductReviewNotes());
            newCase.setObsIdentifiableTarget(cl.getObsIdentifiableTarget());
            newCase.setObsTraceability(cl.getObsTraceability());
            newCase.setObsProductVisibility(cl.getObsProductVisibility());
            newCase.setObsCounterfeitingIndications(cl.getObsCounterfeitingIndications());
            newCase.setObsEvidentiarygaps(cl.getObsEvidentiary_gaps());
            newCase.setQaCompleteness(cl.getQaCompleteness() != null
                    ? cl.getQaCompleteness().name() : null);
            newCase.setQaAccuracy(cl.getQaAccuracy() != null
                    ? cl.getQaAccuracy().name() : null);
            newCase.setQaIndependentInvestigation(cl.getQaIndependentInvestigation() != null
                    ? cl.getQaIndependentInvestigation().name() : null);
            newCase.setQaPriorConfrontation(cl.getQaPriorConfrontation() != null
                    ? cl.getQaPriorConfrontation().name() : null);
            newCase.setQaContaminationRisk(cl.getQaContaminationRisk() != null
                    ? cl.getQaContaminationRisk().name() : null);
            newCase.setAssessmentOverall(cl.getAssessmentOverall() != null
                    ? cl.getAssessmentOverall().name() : null);
            newCase.setAssessmentRationale(cl.getAssessmentRationale());
            newCase.setRecMarketSurvey(cl.getRecMarketSurvey());
            newCase.setRecCovertInvestigation(cl.getRecCovertInvestigation());
            newCase.setRecTestPurchase(cl.getRecTestPurchase());
            newCase.setRecEnforcementAction(cl.getRecEnforcementAction());
            newCase.setRecAdditionalInfo(cl.getRecAdditionalInfo());
            newCase.setRecClosureHold(cl.getRecClosureHold());
            newCase.setRemarks(cl.getRemarks());
            newCase.setCustomDisclaimer(cl.getCustomDisclaimer());

            List<PreReportOnlinePresence> presences =
                    onlinePresenceRepository.findByPrereportId(prereportId);
            presences.forEach(p -> newCase.getOnlinePresences().add(
                    CaseOnlinePresence.builder()
                            .caseEntity(newCase)
                            .platformName(p.getPlatformName())
                            .link(p.getLink())
                            .build()
            ));
        });
    }

    private void mapTrueBuddyLeadData(Case newCase, Long prereportId) {
        trueBuddyLeadRepository.findByPrereportId(prereportId).ifPresent(tb -> {
            newCase.setClientSpocName(tb.getClientSpocName());
            newCase.setClientSpocDesignation(tb.getClientSpocDesignation());
            newCase.setBroadGeography(tb.getBroadGeography());
            newCase.setNatureOfEntity(tb.getNatureOfEntity() != null
                    ? tb.getNatureOfEntity().name() : null);
            newCase.setProductCategory(tb.getProductCategory() != null
                    ? tb.getProductCategory().name() : null);
            newCase.setInfringementType(tb.getInfringementType() != null
                    ? tb.getInfringementType().name() : null);
            newCase.setScopeIprSupplier(tb.getScopeIprSupplier());
            newCase.setScopeIprManufacturer(tb.getScopeIprManufacturer());
            newCase.setScopeIprStockist(tb.getScopeIprStockist());
            newCase.setScopeMarketVerification(tb.getScopeMarketVerification());
            newCase.setScopeEtp(tb.getScopeEtp());
            newCase.setScopeEnforcement(tb.getScopeEnforcement());
            newCase.setIntelNature(tb.getIntelNature() != null
                    ? tb.getIntelNature().name() : null);
            newCase.setSuspectedActivity(tb.getSuspectedActivity() != null
                    ? tb.getSuspectedActivity().name() : null);
            newCase.setProductSegment(tb.getProductSegment() != null
                    ? tb.getProductSegment().name() : null);
            newCase.setSupplyChainStage(tb.getSupplyChainStage() != null
                    ? tb.getSupplyChainStage().name() : null);
            newCase.setRepeatIntelligence(tb.getRepeatIntelligence() != null
                    ? tb.getRepeatIntelligence().name() : null);
            newCase.setMultiBrandRisk(tb.getMultiBrandRisk() != null
                    ? tb.getMultiBrandRisk().name() : null);
            newCase.setVerificationIntelCorroboration(tb.getVerificationIntelCorroboration() != null
                    ? tb.getVerificationIntelCorroboration().name() : null);
            newCase.setVerificationIntelCorroborationNotes(tb.getVerificationIntelCorroborationNotes());
            newCase.setVerificationOsint(tb.getVerificationOsint() != null
                    ? tb.getVerificationOsint().name() : null);
            newCase.setVerificationOsintNotes(tb.getVerificationOsintNotes());
            newCase.setVerificationPatternMapping(tb.getVerificationPatternMapping() != null
                    ? tb.getVerificationPatternMapping().name() : null);
            newCase.setVerificationPatternMappingNotes(tb.getVerificationPatternMappingNotes());
            newCase.setVerificationJurisdiction(tb.getVerificationJurisdiction() != null
                    ? tb.getVerificationJurisdiction().name() : null);
            newCase.setVerificationJurisdictionNotes(tb.getVerificationJurisdictionNotes());
            newCase.setVerificationRiskAssessment(tb.getVerificationRiskAssessment() != null
                    ? tb.getVerificationRiskAssessment().name() : null);
            newCase.setVerificationRiskAssessmentNotes(tb.getVerificationRiskAssessmentNotes());
            newCase.setObsOperationScale(tb.getObsOperationScale() != null
                    ? tb.getObsOperationScale().name() : null);
            newCase.setObsCounterfeitLikelihood(tb.getObsCounterfeitLikelihood() != null
                    ? tb.getObsCounterfeitLikelihood().name() : null);
            newCase.setObsBrandExposure(tb.getObsBrandExposure() != null
                    ? tb.getObsBrandExposure().name() : null);
            newCase.setObsEnforcementSensitivity(tb.getObsEnforcementSensitivity() != null
                    ? tb.getObsEnforcementSensitivity().name() : null);
            newCase.setObsLeakageRisk(tb.getObsLeakageRisk() != null
                    ? tb.getObsLeakageRisk().name() : null);
            newCase.setRiskSourceReliability(tb.getRiskSourceReliability() != null
                    ? tb.getRiskSourceReliability().name() : null);
            newCase.setRiskClientConflict(tb.getRiskClientConflict() != null
                    ? tb.getRiskClientConflict().name() : null);
            newCase.setRiskImmediateAction(tb.getRiskImmediateAction() != null
                    ? tb.getRiskImmediateAction().name() : null);
            newCase.setRiskControlledValidation(tb.getRiskControlledValidation() != null
                    ? tb.getRiskControlledValidation().name() : null);
            newCase.setRiskPrematureDisclosure(tb.getRiskPrematureDisclosure() != null
                    ? tb.getRiskPrematureDisclosure().name() : null);
            newCase.setAssessmentOverall(tb.getAssessmentOverall() != null
                    ? tb.getAssessmentOverall().name() : null);
            newCase.setAssessmentRationale(tb.getAssessmentRationale());
            newCase.setRecCovertValidation(tb.getRecCovertValidation());
            newCase.setRecEtp(tb.getRecEtp());
            newCase.setRecMarketReconnaissance(tb.getRecMarketReconnaissance());
            newCase.setRecEnforcementDeferred(tb.getRecEnforcementDeferred());
            newCase.setRecContinuedMonitoring(tb.getRecContinuedMonitoring());
            newCase.setRecClientSegregation(tb.getRecClientSegregation());
            newCase.setConfidentialityNote(tb.getConfidentialityNote());
            newCase.setRemarks(tb.getRemarks());
            newCase.setCustomDisclaimer(tb.getCustomDisclaimer());
        });
    }

    private String generateCaseNumber() {
        int year = LocalDate.now().getYear();
        Long count = caseRepository.countByYear(year);
        return String.format("CASE-%d-%04d", year, count + 1);
    }

    private String buildCaseTitle(PreReport preReport, String caseNumber) {
        String base = caseNumber + " - " + preReport.getClient().getClientName();
        if (preReport.getLeadType() == LeadType.CLIENT_LEAD) {
            return clientLeadRepository.findByPrereportId(preReport.getId())
                    .map(cl -> cl.getEntityName() != null
                            ? base + " | " + cl.getEntityName()
                            : base)
                    .orElse(base);
        } else {
            return trueBuddyLeadRepository.findByPrereportId(preReport.getId())
                    .map(tb -> tb.getBroadGeography() != null
                            ? base + " | " + tb.getBroadGeography()
                            : base)
                    .orElse(base);
        }
    }

    private String resolveProductNames(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) return "";
        return clientProductRepository.findAllById(productIds)
                .stream()
                .map(ClientProduct::getProductName)
                .collect(Collectors.joining(", "));
    }

    private CaseResponse mapToCaseResponse(Case c) {
        List<String> employees = (c.getAssignedEmployees() != null && !c.getAssignedEmployees().isBlank())
                ? Arrays.asList(c.getAssignedEmployees().split(","))
                : new ArrayList<>();

        List<CaseOnlinePresenceResponse> presences = c.getOnlinePresences().stream()
                .map(p -> CaseOnlinePresenceResponse.builder()
                        .id(p.getId())
                        .platformName(p.getPlatformName())
                        .link(p.getLink())
                        .build())
                .collect(Collectors.toList());

        List<CaseUpdateResponse> updates = c.getUpdates().stream()
                .map(u -> CaseUpdateResponse.builder()
                        .id(u.getId())
                        .updateDate(u.getUpdateDate())
                        .updatedBy(u.getUpdatedBy())
                        .description(u.getDescription())
                        .build())
                .collect(Collectors.toList());

        return CaseResponse.builder()
                .id(c.getId())
                .caseNumber(c.getCaseNumber())
                .prereportReportId(c.getPrereportReportId())
                .leadType(c.getLeadType())
                .caseTitle(c.getCaseTitle())
                .priority(c.getPriority())
                .status(c.getStatus())
                .caseType(c.getCaseType())
                .dateOpened(c.getDateOpened())
                .dateClosed(c.getDateClosed())
                .clientId(c.getClientId())
                .clientName(c.getClientName())
                .clientProduct(c.getClientProduct())
                .clientSpocName(c.getClientSpocName())
                .clientSpocContact(c.getClientSpocContact())
                .clientSpocDesignation(c.getClientSpocDesignation())
                .clientEmail(c.getClientEmail())
                .entityName(c.getEntityName())
                .suspectName(c.getSuspectName())
                .contactNumbers(c.getContactNumbers())
                .addressLine1(c.getAddressLine1())
                .addressLine2(c.getAddressLine2())
                .city(c.getCity())
                .state(c.getState())
                .pincode(c.getPincode())
                .productDetails(c.getProductDetails())
                .photosProvided(c.getPhotosProvided())
                .videoProvided(c.getVideoProvided())
                .invoiceAvailable(c.getInvoiceAvailable())
                .sourceNarrative(c.getSourceNarrative())
                .scopeDueDiligence(c.getScopeDueDiligence())
                .scopeIprRetailer(c.getScopeIprRetailer())
                .scopeIprSupplier(c.getScopeIprSupplier())
                .scopeIprManufacturer(c.getScopeIprManufacturer())
                .scopeOnlinePurchase(c.getScopeOnlinePurchase())
                .scopeOfflinePurchase(c.getScopeOfflinePurchase())
                .scopeIprStockist(c.getScopeIprStockist())
                .scopeMarketVerification(c.getScopeMarketVerification())
                .scopeEtp(c.getScopeEtp())
                .scopeEnforcement(c.getScopeEnforcement())
                .broadGeography(c.getBroadGeography())
                .natureOfEntity(c.getNatureOfEntity())
                .productCategory(c.getProductCategory())
                .infringementType(c.getInfringementType())
                .intelNature(c.getIntelNature())
                .suspectedActivity(c.getSuspectedActivity())
                .productSegment(c.getProductSegment())
                .supplyChainStage(c.getSupplyChainStage())
                .repeatIntelligence(c.getRepeatIntelligence())
                .multiBrandRisk(c.getMultiBrandRisk())
                .verificationClientDiscussion(c.getVerificationClientDiscussion())
                .verificationClientDiscussionNotes(c.getVerificationClientDiscussionNotes())
                .verificationOsint(c.getVerificationOsint())
                .verificationOsintNotes(c.getVerificationOsintNotes())
                .verificationMarketplace(c.getVerificationMarketplace())
                .verificationMarketplaceNotes(c.getVerificationMarketplaceNotes())
                .verificationPretextCalling(c.getVerificationPretextCalling())
                .verificationPretextCallingNotes(c.getVerificationPretextCallingNotes())
                .verificationProductReview(c.getVerificationProductReview())
                .verificationProductReviewNotes(c.getVerificationProductReviewNotes())
                .verificationIntelCorroboration(c.getVerificationIntelCorroboration())
                .verificationIntelCorroborationNotes(c.getVerificationIntelCorroborationNotes())
                .verificationPatternMapping(c.getVerificationPatternMapping())
                .verificationPatternMappingNotes(c.getVerificationPatternMappingNotes())
                .verificationJurisdiction(c.getVerificationJurisdiction())
                .verificationJurisdictionNotes(c.getVerificationJurisdictionNotes())
                .verificationRiskAssessment(c.getVerificationRiskAssessment())
                .verificationRiskAssessmentNotes(c.getVerificationRiskAssessmentNotes())
                .obsIdentifiableTarget(c.getObsIdentifiableTarget())
                .obsTraceability(c.getObsTraceability())
                .obsProductVisibility(c.getObsProductVisibility())
                .obsCounterfeitingIndications(c.getObsCounterfeitingIndications())
                .obsEvidentiarygaps(c.getObsEvidentiarygaps())
                .obsOperationScale(c.getObsOperationScale())
                .obsCounterfeitLikelihood(c.getObsCounterfeitLikelihood())
                .obsBrandExposure(c.getObsBrandExposure())
                .obsEnforcementSensitivity(c.getObsEnforcementSensitivity())
                .obsLeakageRisk(c.getObsLeakageRisk())
                .qaCompleteness(c.getQaCompleteness())
                .qaAccuracy(c.getQaAccuracy())
                .qaIndependentInvestigation(c.getQaIndependentInvestigation())
                .qaPriorConfrontation(c.getQaPriorConfrontation())
                .qaContaminationRisk(c.getQaContaminationRisk())
                .riskSourceReliability(c.getRiskSourceReliability())
                .riskClientConflict(c.getRiskClientConflict())
                .riskImmediateAction(c.getRiskImmediateAction())
                .riskControlledValidation(c.getRiskControlledValidation())
                .riskPrematureDisclosure(c.getRiskPrematureDisclosure())
                .assessmentOverall(c.getAssessmentOverall())
                .assessmentRationale(c.getAssessmentRationale())
                .recMarketSurvey(c.getRecMarketSurvey())
                .recCovertInvestigation(c.getRecCovertInvestigation())
                .recTestPurchase(c.getRecTestPurchase())
                .recEnforcementAction(c.getRecEnforcementAction())
                .recAdditionalInfo(c.getRecAdditionalInfo())
                .recClosureHold(c.getRecClosureHold())
                .recCovertValidation(c.getRecCovertValidation())
                .recEtp(c.getRecEtp())
                .recMarketReconnaissance(c.getRecMarketReconnaissance())
                .recEnforcementDeferred(c.getRecEnforcementDeferred())
                .recContinuedMonitoring(c.getRecContinuedMonitoring())
                .recClientSegregation(c.getRecClientSegregation())
                .confidentialityNote(c.getConfidentialityNote())
                .remarks(c.getRemarks())
                .customDisclaimer(c.getCustomDisclaimer())
                .assignedEmployees(employees)
                .estimatedCompletionDate(c.getEstimatedCompletionDate())
                .actualCompletionDate(c.getActualCompletionDate())
                .onlinePresences(presences)
                .updates(updates)
                .createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private CaseListItemResponse mapToCaseListItemResponse(Case c) {
        return CaseListItemResponse.builder()
                .id(c.getId())
                .caseNumber(c.getCaseNumber())
                .caseTitle(c.getCaseTitle())
                .priority(c.getPriority())
                .status(c.getStatus())
                .caseType(c.getCaseType())
                .leadType(c.getLeadType())
                .clientName(c.getClientName())
                .clientProduct(c.getClientProduct())
                .dateOpened(c.getDateOpened())
                .estimatedCompletionDate(c.getEstimatedCompletionDate())
                .createdBy(c.getCreatedBy())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
