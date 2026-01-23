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
import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import com.tbcpl.workforce.operation.prereport.repository.PreReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    public PreReportService(PreReportRepository preReportRepository,
                            ClientRepository clientRepository,
                            ClientProductRepository clientProductRepository,
                            @Lazy PreReportClientLeadService clientLeadService,
                            @Lazy PreReportTrueBuddyLeadService trueBuddyLeadService) {
        this.preReportRepository = preReportRepository;
        this.clientRepository = clientRepository;
        this.clientProductRepository = clientProductRepository;
        this.clientLeadService = clientLeadService;
        this.trueBuddyLeadService = trueBuddyLeadService;
    }

    @Transactional
    public PreReportResponse initializeReport(PreReportInitializeRequest request, String createdBy) {
        log.info("Initializing pre-report for client: {}, leadType: {}", request.getClientId(), request.getLeadType());

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
                .createdBy(createdBy)
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
                .clientId(preReport.getClient().getClientId())  // ✅ FIXED
                .clientName(preReport.getClient().getClientName())  // ✅ ADDED
                .productIds(preReport.getProductIds())
                .productNames(productNames)  // ✅ ADDED
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
}
