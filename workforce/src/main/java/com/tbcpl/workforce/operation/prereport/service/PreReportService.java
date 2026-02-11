package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.admin.entity.ClientProduct;
import com.tbcpl.workforce.admin.repository.ClientProductRepository;
import com.tbcpl.workforce.admin.repository.ClientRepository;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportInitializeRequest;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportStatusUpdateRequest;
import com.tbcpl.workforce.operation.prereport.dto.response.PreReportDetailResponse;
import com.tbcpl.workforce.operation.prereport.dto.response.PreReportListResponse;
import com.tbcpl.workforce.operation.prereport.dto.response.PreReportResponse;
import com.tbcpl.workforce.operation.prereport.entity.PreReport;
import com.tbcpl.workforce.operation.prereport.entity.PreReportStepTracking;
import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import com.tbcpl.workforce.operation.prereport.repository.PreReportRepository;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportRequestChangesRequest;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportRejectRequest;
import com.tbcpl.workforce.operation.prereport.dto.response.PreReportStepStatusResponse;
import com.tbcpl.workforce.operation.prereport.dto.response.StepStatusDetail;
import com.tbcpl.workforce.operation.prereport.entity.enums.StepStatus;
import com.tbcpl.workforce.operation.prereport.repository.PreReportStepTrackingRepository;
import com.tbcpl.workforce.auth.entity.Employee;  // ✅ ADDED
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PreReportService {

    private final PreReportRepository preReportRepository;
    private final ClientRepository clientRepository;
    private final ClientProductRepository clientProductRepository;
    private final PreReportClientLeadService clientLeadService;
    private final PreReportTrueBuddyLeadService trueBuddyLeadService;
    private final PreReportStepTrackingRepository stepTrackingRepository;
    private final EmployeeRepository employeeRepository;

    public PreReportService(PreReportRepository preReportRepository,
                            ClientRepository clientRepository,
                            ClientProductRepository clientProductRepository,
                            @Lazy PreReportClientLeadService clientLeadService,
                            @Lazy PreReportTrueBuddyLeadService trueBuddyLeadService,
                            PreReportStepTrackingRepository stepTrackingRepository,
                            EmployeeRepository employeeRepository) {
        this.preReportRepository = preReportRepository;
        this.clientRepository = clientRepository;
        this.clientProductRepository = clientProductRepository;
        this.clientLeadService = clientLeadService;
        this.trueBuddyLeadService = trueBuddyLeadService;
        this.stepTrackingRepository = stepTrackingRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public PreReportResponse initializeReport(PreReportInitializeRequest request, String empId) {
        log.info("Initializing pre-report for client: {}, leadType: {}", request.getClientId(), request.getLeadType());

        // ✅ ADDED: Get employee by empId to retrieve employee ID
        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with empId: " + empId));

        log.info("Employee found: ID={}, empId={}, name={}", employee.getId(), employee.getEmpId(), employee.getFullName());

        // ✅ ADDED: Validate client exists
        Client client = clientRepository.findActiveClientById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + request.getClientId()));

        // ✅ ADDED: Validate products belong to client
        validateProductsBelongToClient(request.getClientId(), request.getProductIds());

        String reportId = generateReportId();

        PreReport preReport = PreReport.builder()
                .reportId(reportId)
                .client(client)  // ✅ FIXED: Use client object instead of clientId
                .productIds(request.getProductIds())
                .leadType(request.getLeadType())
                .reportStatus(ReportStatus.DRAFT)
                .currentStep(0)
                .createdBy(employee.getId())
                .isDeleted(false)
                .build();

        PreReport savedReport = preReportRepository.save(preReport);

        // Initialize lead-specific table based on type
        if (request.getLeadType() == LeadType.CLIENT_LEAD) {
            clientLeadService.initializeClientLead(savedReport.getId());
        } else {
            trueBuddyLeadService.initializeTrueBuddyLead(savedReport.getId());
        }

        log.info("Pre-report initialized successfully with ID: {}", reportId);
        return mapToResponse(savedReport);
    }

    @Transactional(readOnly = true)
    public PreReportResponse getReportByReportId(String reportId) {
        log.info("Fetching pre-report with reportId: {}", reportId);

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        return mapToResponse(preReport);
    }

    @Transactional(readOnly = true)
    public PreReportDetailResponse getReportDetailByReportId(String reportId) {
        log.info("Fetching detailed pre-report with reportId: {}", reportId);

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        PreReportDetailResponse.PreReportDetailResponseBuilder responseBuilder = PreReportDetailResponse.builder()
                .preReport(mapToResponse(preReport));

        if (preReport.getLeadType() == LeadType.CLIENT_LEAD) {
            responseBuilder.clientLeadData(clientLeadService.getClientLeadByPrereportId(preReport.getId()));
        } else {
            responseBuilder.trueBuddyLeadData(trueBuddyLeadService.getTrueBuddyLeadByPrereportId(preReport.getId()));
        }

        return responseBuilder.build();
    }

    @Transactional(readOnly = true)
    public PreReportListResponse getAllReports(int page, int size) {
        log.info("Fetching all pre-reports - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<PreReport> reportPage = preReportRepository.findAllActiveReports(pageable);

        return buildListResponse(reportPage);
    }

    @Transactional(readOnly = true)
    public PreReportListResponse getReportsByClientId(Long clientId, int page, int size) {
        log.info("Fetching pre-reports for clientId: {} - page: {}, size: {}", clientId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<PreReport> reportPage = preReportRepository.findByClientId(clientId, pageable);

        return buildListResponse(reportPage);
    }

    @Transactional(readOnly = true)
    public PreReportListResponse getReportsByLeadType(LeadType leadType, int page, int size) {
        log.info("Fetching pre-reports by leadType: {} - page: {}, size: {}", leadType, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<PreReport> reportPage = preReportRepository.findByLeadType(leadType, pageable);

        return buildListResponse(reportPage);
    }

    @Transactional(readOnly = true)
    public PreReportListResponse getReportsByStatus(ReportStatus status, int page, int size) {
        log.info("Fetching pre-reports by status: {} - page: {}, size: {}", status, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<PreReport> reportPage = preReportRepository.findByReportStatus(status, pageable);

        return buildListResponse(reportPage);
    }

    @Transactional(readOnly = true)
    public PreReportListResponse getReportsByCreatedBy(String createdBy, int page, int size) {
        log.info("Fetching pre-reports by createdBy: {} - page: {}, size: {}", createdBy, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<PreReport> reportPage = preReportRepository.findByCreatedBy(createdBy, pageable);

        return buildListResponse(reportPage);
    }

    @Transactional
    public PreReportResponse updateReportStatus(String reportId, PreReportStatusUpdateRequest request) {
        log.info("Updating report status for reportId: {} to {}", reportId, request.getReportStatus());

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        preReport.setReportStatus(request.getReportStatus());
        preReport.setUpdatedAt(LocalDateTime.now());

        PreReport updatedReport = preReportRepository.save(preReport);

        log.info("Report status updated successfully for reportId: {}", reportId);
        return mapToResponse(updatedReport);
    }

    @Transactional
    public void updateCurrentStep(String reportId, int stepNumber) {
        log.info("Updating current step for reportId: {} to step: {}", reportId, stepNumber);

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        preReport.setCurrentStep(stepNumber);
        preReport.setUpdatedAt(LocalDateTime.now());

        preReportRepository.save(preReport);
    }

    @Transactional
    public void softDeleteReport(String reportId) {
        log.info("Soft deleting pre-report with reportId: {}", reportId);

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        preReport.setIsDeleted(true);
        preReport.setUpdatedAt(LocalDateTime.now());

        preReportRepository.save(preReport);

        log.info("Pre-report soft deleted successfully: {}", reportId);
    }
    @Transactional
    public PreReportResponse submitForApproval(String reportId) {
        log.info("Submitting report for approval: {}", reportId);

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        // Validation: Can only submit if status is IN_PROGRESS or REQUESTED_FOR_CHANGES
        if (preReport.getReportStatus() != ReportStatus.IN_PROGRESS
                && preReport.getReportStatus() != ReportStatus.REQUESTED_FOR_CHANGES) {
            throw new RuntimeException("Report can only be submitted when status is IN_PROGRESS or REQUESTED_FOR_CHANGES");
        }

        preReport.setReportStatus(ReportStatus.WAITING_FOR_APPROVAL);
        preReport.setChangeComments(null); // Clear previous comments
        preReport.setUpdatedAt(LocalDateTime.now());

        PreReport updatedReport = preReportRepository.save(preReport);

        log.info("Report submitted for approval: {}", reportId);
        return mapToResponse(updatedReport);
    }

    @Transactional
    public PreReportResponse approveReport(String reportId) {
        log.info("Approving report: {}", reportId);



        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        // Validation: Can only approve if status is WAITING_FOR_APPROVAL
        if (preReport.getReportStatus() != ReportStatus.WAITING_FOR_APPROVAL) {
            throw new RuntimeException("Report can only be approved when status is WAITING_FOR_APPROVAL");
        }

        preReport.setReportStatus(ReportStatus.READY_FOR_CREATE_CASE);
        preReport.setUpdatedAt(LocalDateTime.now());

        PreReport updatedReport = preReportRepository.save(preReport);

        log.info("Report approved: {}", reportId);
        return mapToResponse(updatedReport);
    }

    @Transactional
    public PreReportResponse requestChanges(String reportId, PreReportRequestChangesRequest request) {
        log.info("Requesting changes for report: {}", reportId);

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        // Validation: Can only request changes if status is WAITING_FOR_APPROVAL
        if (preReport.getReportStatus() != ReportStatus.WAITING_FOR_APPROVAL) {
            throw new RuntimeException("Changes can only be requested when status is WAITING_FOR_APPROVAL");
        }

        preReport.setReportStatus(ReportStatus.REQUESTED_FOR_CHANGES);
        preReport.setChangeComments(request.getChangeComments());
        preReport.setUpdatedAt(LocalDateTime.now());

        PreReport updatedReport = preReportRepository.save(preReport);

        log.info("Changes requested for report: {}", reportId);
        return mapToResponse(updatedReport);
    }

    @Transactional
    public PreReportResponse rejectReport(String reportId, PreReportRejectRequest request) {
        log.info("Rejecting report: {}", reportId);

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        // Validation: Can only reject if status is WAITING_FOR_APPROVAL
        if (preReport.getReportStatus() != ReportStatus.WAITING_FOR_APPROVAL) {
            throw new RuntimeException("Report can only be rejected when status is WAITING_FOR_APPROVAL");
        }

        preReport.setReportStatus(ReportStatus.DISAPPROVED_BY_CLIENT);
        preReport.setRejectionReason(request.getRejectionReason());
        preReport.setUpdatedAt(LocalDateTime.now());

        PreReport updatedReport = preReportRepository.save(preReport);

        log.info("Report rejected: {}", reportId);
        return mapToResponse(updatedReport);
    }

    @Transactional
    public PreReportResponse resubmitReport(String reportId) {
        log.info("Re-submitting report after changes: {}", reportId);

        PreReport preReport = preReportRepository.findByReportIdAndIsDeletedFalse(reportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + reportId));

        // Validation: Can only resubmit if status is REQUESTED_FOR_CHANGES
        if (preReport.getReportStatus() != ReportStatus.REQUESTED_FOR_CHANGES) {
            throw new RuntimeException("Report can only be resubmitted when status is REQUESTED_FOR_CHANGES");
        }

        preReport.setReportStatus(ReportStatus.WAITING_FOR_APPROVAL);
        preReport.setChangeComments(null); // Clear comments after resubmission
        preReport.setUpdatedAt(LocalDateTime.now());

        PreReport updatedReport = preReportRepository.save(preReport);

        log.info("Report resubmitted: {}", reportId);
        return mapToResponse(updatedReport);
    }

    @Transactional(readOnly = true)
    public PreReportStepStatusResponse getStepStatus(Long prereportId) {
        log.info("Fetching step status for prereportId: {}", prereportId);

        PreReport preReport = preReportRepository.findById(prereportId)
                .orElseThrow(() -> new RuntimeException("Pre-report not found with ID: " + prereportId));

        List<StepStatusDetail> stepStatuses;

        if (preReport.getLeadType() == LeadType.CLIENT_LEAD) {
            stepStatuses = getClientLeadStepStatuses(prereportId);
        } else {
            stepStatuses = getTrueBuddyLeadStepStatuses(prereportId);
        }

        return PreReportStepStatusResponse.builder()
                .prereportId(preReport.getId())
                .reportId(preReport.getReportId())
                .leadType(preReport.getLeadType())
                .reportStatus(preReport.getReportStatus())
                .currentStep(preReport.getCurrentStep())
                .canEdit(preReport.canEdit())
                .changeComments(preReport.getChangeComments())
                .rejectionReason(preReport.getRejectionReason())
                .steps(stepStatuses)
                .build();
    }

    @Transactional
    public void markStepAsCompleted(Long prereportId, int stepNumber) {
        log.info("Marking step {} as COMPLETED for prereportId: {}", stepNumber, prereportId);

        PreReportStepTracking tracking = stepTrackingRepository
                .findByPrereportIdAndStepNumber(prereportId, stepNumber)
                .orElse(PreReportStepTracking.builder()
                        .prereportId(prereportId)
                        .stepNumber(stepNumber)
                        .build());

        tracking.setStatus(StepStatus.COMPLETED);
        stepTrackingRepository.save(tracking);
    }

    @Transactional
    public void markStepAsSkipped(Long prereportId, int stepNumber) {
        log.info("Marking step {} as PENDING (skipped) for prereportId: {}", stepNumber, prereportId);

        // When skipped, we DON'T create a tracking record
        // or we create one with PENDING status
        // This way, the step remains PENDING until actually filled
    }


    // ✅ ADDED: Validation method
    private void validateProductsBelongToClient(Long clientId, List<Long> productIds) {
        List<ClientProduct> clientProducts = clientProductRepository.findActiveProductsByClientId(clientId);
        List<Long> validProductIds = clientProducts.stream()
                .map(ClientProduct::getId)
                .collect(Collectors.toList());

        boolean allValid = productIds.stream().allMatch(validProductIds::contains);

        if (!allValid) {
            throw new RuntimeException("One or more products do not belong to the selected client");
        }
    }

    private String generateReportId() {
        int currentYear = Year.now().getValue();
        Long count = preReportRepository.countByYear(currentYear);
        long nextNumber = (count != null ? count : 0L) + 1;

        return String.format("PRE-%d-%04d", currentYear, nextNumber);
    }

    // ✅ FIXED: Map response with client name and product names
    private PreReportResponse mapToResponse(PreReport preReport) {
        // Fetch product names
        List<String> productNames = preReport.getProductIds().stream()
                .map(productId -> clientProductRepository.findActiveProductById(productId)
                        .map(ClientProduct::getProductName)
                        .orElse("Unknown Product"))
                .collect(Collectors.toList());

        return PreReportResponse.builder()
                .id(preReport.getId())
                .reportId(preReport.getReportId())
                .clientId(preReport.getClient().getClientId())
                .clientName(preReport.getClient().getClientName())
                .productIds(preReport.getProductIds())
                .productNames(productNames)
                .leadType(preReport.getLeadType())
                .reportStatus(preReport.getReportStatus())
                .currentStep(preReport.getCurrentStep())
                .createdBy(preReport.getCreatedBy())
                .createdAt(preReport.getCreatedAt())
                .updatedAt(preReport.getUpdatedAt())
                .build();
    }

    private PreReportListResponse buildListResponse(Page<PreReport> reportPage) {
        List<PreReportResponse> reports = reportPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PreReportListResponse.builder()
                .reports(reports)
                .currentPage(reportPage.getNumber())
                .totalPages(reportPage.getTotalPages())
                .totalElements(reportPage.getTotalElements())
                .pageSize(reportPage.getSize())
                .build();
    }

    private List<StepStatusDetail> getClientLeadStepStatuses(Long prereportId) {
        try {
            var clientLead = clientLeadService.getClientLeadByPrereportId(prereportId);

            // Fetch step tracking data
            List<PreReportStepTracking> trackingList = stepTrackingRepository
                    .findByPrereportIdOrderByStepNumberAsc(prereportId);

            Map<Integer, StepStatus> trackingMap = trackingList.stream()
                    .collect(Collectors.toMap(
                            PreReportStepTracking::getStepNumber,
                            PreReportStepTracking::getStatus
                    ));

            return List.of(
                    StepStatusDetail.builder().stepNumber(1).stepName("Basic Information")
                            .status(getStepStatus(trackingMap, 1, clientLead.getDateInfoReceived() != null)).build(),
                    StepStatusDetail.builder().stepNumber(2).stepName("Scope Selection")
                            .status(getStepStatus(trackingMap, 2, clientLead.getScopeDueDiligence() != null)).build(),
                    StepStatusDetail.builder().stepNumber(3).stepName("Target Details")
                            .status(getStepStatus(trackingMap, 3, clientLead.getEntityName() != null)).build(),
                    StepStatusDetail.builder().stepNumber(4).stepName("Verification")
                            .status(getStepStatus(trackingMap, 4, clientLead.getVerificationClientDiscussion() != null)).build(),
                    StepStatusDetail.builder().stepNumber(5).stepName("Observations")
                            .status(getStepStatus(trackingMap, 5, clientLead.getObsIdentifiableTarget() != null)).build(),
                    StepStatusDetail.builder().stepNumber(6).stepName("Quality Assessment")
                            .status(getStepStatus(trackingMap, 6, clientLead.getQaCompleteness() != null)).build(),
                    StepStatusDetail.builder().stepNumber(7).stepName("Assessment")
                            .status(getStepStatus(trackingMap, 7, clientLead.getAssessmentOverall() != null)).build(),
                    StepStatusDetail.builder().stepNumber(8).stepName("Recommendations")
                            .status(getStepStatus(trackingMap, 8, clientLead.getRecMarketSurvey() != null)).build(),
                    StepStatusDetail.builder().stepNumber(9).stepName("Remarks")
                            .status(getStepStatus(trackingMap, 9, clientLead.getRemarks() != null)).build(),
                    StepStatusDetail.builder().stepNumber(10).stepName("Disclaimer")
                            .status(getStepStatus(trackingMap, 10, clientLead.getCustomDisclaimer() != null)).build()
            );
        } catch (Exception e) {
            return createPendingSteps(10);
        }
    }




    private List<StepStatusDetail> getTrueBuddyLeadStepStatuses(Long prereportId) {
        try {
            var trueBuddyLead = trueBuddyLeadService.getTrueBuddyLeadByPrereportId(prereportId);

            // Fetch step tracking data
            List<PreReportStepTracking> trackingList = stepTrackingRepository
                    .findByPrereportIdOrderByStepNumberAsc(prereportId);

            Map<Integer, StepStatus> trackingMap = trackingList.stream()
                    .collect(Collectors.toMap(
                            PreReportStepTracking::getStepNumber,
                            PreReportStepTracking::getStatus
                    ));

            return List.of(
                    StepStatusDetail.builder().stepNumber(1).stepName("Basic Information")
                            .status(getStepStatus(trackingMap, 1, trueBuddyLead.getDateInternalLeadGeneration() != null)).build(),
                    StepStatusDetail.builder().stepNumber(2).stepName("Scope")
                            .status(getStepStatus(trackingMap, 2, trueBuddyLead.getScopeIprSupplier() != null)).build(),
                    StepStatusDetail.builder().stepNumber(3).stepName("Intelligence Nature")
                            .status(getStepStatus(trackingMap, 3, trueBuddyLead.getIntelNature() != null)).build(),
                    StepStatusDetail.builder().stepNumber(4).stepName("Verification")
                            .status(getStepStatus(trackingMap, 4, trueBuddyLead.getVerificationIntelCorroboration() != null)).build(),
                    StepStatusDetail.builder().stepNumber(5).stepName("Observations")
                            .status(getStepStatus(trackingMap, 5, trueBuddyLead.getObsOperationScale() != null)).build(),
                    StepStatusDetail.builder().stepNumber(6).stepName("Risk Assessment")
                            .status(getStepStatus(trackingMap, 6, trueBuddyLead.getRiskSourceReliability() != null)).build(),
                    StepStatusDetail.builder().stepNumber(7).stepName("Assessment")
                            .status(getStepStatus(trackingMap, 7, trueBuddyLead.getAssessmentOverall() != null)).build(),
                    StepStatusDetail.builder().stepNumber(8).stepName("Recommendations")
                            .status(getStepStatus(trackingMap, 8, trueBuddyLead.getRecCovertValidation() != null)).build(),
                    StepStatusDetail.builder().stepNumber(9).stepName("Confidentiality")
                            .status(getStepStatus(trackingMap, 9, trueBuddyLead.getConfidentialityNote() != null)).build(),
                    StepStatusDetail.builder().stepNumber(10).stepName("Remarks")
                            .status(getStepStatus(trackingMap, 10, trueBuddyLead.getRemarks() != null)).build(),
                    StepStatusDetail.builder().stepNumber(11).stepName("Disclaimer")
                            .status(getStepStatus(trackingMap, 11, trueBuddyLead.getCustomDisclaimer() != null)).build()
            );
        } catch (Exception e) {
            return createPendingSteps(11);
        }
    }

    // Helper method to determine step status
    private StepStatus getStepStatus(Map<Integer, StepStatus> trackingMap, int stepNumber, boolean hasData) {
        // If explicitly tracked, use that status
        if (trackingMap.containsKey(stepNumber)) {
            return trackingMap.get(stepNumber);
        }

        // Otherwise, determine by data presence
        return hasData ? StepStatus.COMPLETED : StepStatus.PENDING;
    }



    private List<StepStatusDetail> createPendingSteps(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(i -> StepStatusDetail.builder()
                        .stepNumber(i)
                        .stepName("Step " + i)
                        .status(StepStatus.PENDING)
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

}
