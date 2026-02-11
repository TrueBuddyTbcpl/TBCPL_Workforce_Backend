package com.tbcpl.workforce.operation.prereport.controller;

import com.tbcpl.workforce.operation.prereport.dto.request.PreReportInitializeRequest;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportStatusUpdateRequest;
import com.tbcpl.workforce.operation.prereport.dto.response.PreReportDetailResponse;
import com.tbcpl.workforce.operation.prereport.dto.response.PreReportListResponse;
import com.tbcpl.workforce.operation.prereport.dto.response.PreReportResponse;
import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import com.tbcpl.workforce.operation.prereport.service.PreReportService;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportRequestChangesRequest;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportRejectRequest;
import com.tbcpl.workforce.operation.prereport.dto.response.PreReportStepStatusResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/operation/prereport")
@Slf4j
public class PreReportController {

    private final PreReportService preReportService;

    public PreReportController(PreReportService preReportService) {
        this.preReportService = preReportService;
    }

    @PostMapping("/initialize")
    public ResponseEntity<PreReportResponse> initializeReport(
            @Valid @RequestBody PreReportInitializeRequest request,
            Authentication authentication) {
        log.info("POST /api/v1/operation/prereport/initialize - Initializing pre-report");

        // ✅ authentication.getName() now returns empId
        String createdBy = authentication.getName();
        log.info("✅ Report created by empId: {}", createdBy);

        PreReportResponse response = preReportService.initializeReport(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{reportId}")
    public ResponseEntity<PreReportResponse> getReportByReportId(@PathVariable String reportId) {
        log.info("GET /api/v1/operation/prereport/{} - Fetching pre-report", reportId);

        PreReportResponse response = preReportService.getReportByReportId(reportId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reportId}/detail")
    public ResponseEntity<PreReportDetailResponse> getReportDetailByReportId(@PathVariable String reportId) {
        log.info("GET /api/v1/operation/prereport/{}/detail - Fetching detailed pre-report", reportId);

        PreReportDetailResponse response = preReportService.getReportDetailByReportId(reportId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<PreReportListResponse> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/operation/prereport/list - Fetching all reports (page: {}, size: {})", page, size);

        PreReportListResponse response = preReportService.getAllReports(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/client/{clientId}")
    public ResponseEntity<PreReportListResponse> getReportsByClientId(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/operation/prereport/list/client/{} - Fetching reports by client", clientId);

        PreReportListResponse response = preReportService.getReportsByClientId(clientId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/lead-type/{leadType}")
    public ResponseEntity<PreReportListResponse> getReportsByLeadType(
            @PathVariable LeadType leadType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/operation/prereport/list/lead-type/{} - Fetching reports by lead type", leadType);

        PreReportListResponse response = preReportService.getReportsByLeadType(leadType, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/status/{status}")
    public ResponseEntity<PreReportListResponse> getReportsByStatus(
            @PathVariable ReportStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/operation/prereport/list/status/{} - Fetching reports by status", status);

        PreReportListResponse response = preReportService.getReportsByStatus(status, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/created-by/{createdBy}")
    public ResponseEntity<PreReportListResponse> getReportsByCreatedBy(
            @PathVariable String createdBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("GET /api/v1/operation/prereport/list/created-by/{} - Fetching reports by creator", createdBy);

        PreReportListResponse response = preReportService.getReportsByCreatedBy(createdBy, page, size);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reportId}/status")
    public ResponseEntity<PreReportResponse> updateReportStatus(
            @PathVariable String reportId,
            @Valid @RequestBody PreReportStatusUpdateRequest request) {
        log.info("PATCH /api/v1/operation/prereport/{}/status - Updating report status", reportId);

        PreReportResponse response = preReportService.updateReportStatus(reportId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> softDeleteReport(@PathVariable String reportId) {
        log.info("DELETE /api/v1/operation/prereport/{} - Soft deleting report", reportId);

        preReportService.softDeleteReport(reportId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reportId}/submit")
    public ResponseEntity<PreReportResponse> submitForApproval(@PathVariable String reportId) {
        log.info("POST /api/v1/operation/prereport/{}/submit - Submit report for approval", reportId);
        PreReportResponse response = preReportService.submitForApproval(reportId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reportId}/approve")
    public ResponseEntity<PreReportResponse> approveReport(@PathVariable String reportId) {
        log.info("POST /api/v1/operation/prereport/{}/approve - Approve report", reportId);
        PreReportResponse response = preReportService.approveReport(reportId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reportId}/request-changes")
    public ResponseEntity<PreReportResponse> requestChanges(
            @PathVariable String reportId,
            @Valid @RequestBody PreReportRequestChangesRequest request) {
        log.info("POST /api/v1/operation/prereport/{}/request-changes - Request changes", reportId);
        PreReportResponse response = preReportService.requestChanges(reportId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reportId}/reject")
    public ResponseEntity<PreReportResponse> rejectReport(
            @PathVariable String reportId,
            @Valid @RequestBody PreReportRejectRequest request) {
        log.info("POST /api/v1/operation/prereport/{}/reject - Reject report", reportId);
        PreReportResponse response = preReportService.rejectReport(reportId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reportId}/resubmit")
    public ResponseEntity<PreReportResponse> resubmitReport(@PathVariable String reportId) {
        log.info("POST /api/v1/operation/prereport/{}/resubmit - Resubmit report after changes", reportId);
        PreReportResponse response = preReportService.resubmitReport(reportId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{prereportId}/step-status")
    public ResponseEntity<PreReportStepStatusResponse> getStepStatus(@PathVariable Long prereportId) {
        log.info("GET /api/v1/operation/prereport/{}/step-status - Get step status", prereportId);
        PreReportStepStatusResponse response = preReportService.getStepStatus(prereportId);
        return ResponseEntity.ok(response);
    }



}
