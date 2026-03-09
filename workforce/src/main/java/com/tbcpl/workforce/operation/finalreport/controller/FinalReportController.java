package com.tbcpl.workforce.operation.finalreport.controller;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.operation.finalreport.dto.request.CreateFinalReportRequest;
import com.tbcpl.workforce.operation.finalreport.dto.request.FinalReportStatusUpdateRequest;
import com.tbcpl.workforce.operation.finalreport.dto.request.UpdateFinalReportRequest;
import com.tbcpl.workforce.operation.finalreport.dto.response.CaseReportPrefillResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.FinalReportListItemResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.FinalReportResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.ImageUploadResponse;
import com.tbcpl.workforce.operation.finalreport.entity.enums.FinalReportStatus;
import com.tbcpl.workforce.operation.finalreport.service.FinalReportService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/v1/operation/finalreport")
@Slf4j
public class FinalReportController {

    private final FinalReportService finalReportService;

    public FinalReportController(FinalReportService finalReportService) {
        this.finalReportService = finalReportService;
    }

    // ── Prefill ──────────────────────────────────────────────────────────
    @GetMapping("/prefill/{caseId}")
    public ResponseEntity<ApiResponse<CaseReportPrefillResponse>> getCaseReportPrefill(
            @PathVariable Long caseId) {
        log.info("GET /prefill/{}", caseId);
        return ResponseEntity.ok(ApiResponse.success(
                "Prefill data fetched successfully",
                finalReportService.getCaseReportPrefill(caseId)
        ));
    }

    // ── Image upload (multiple, ratio-validated) ─────────────────────────
    @PostMapping(value = "/{caseId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ImageUploadResponse>> uploadImages(
            @PathVariable Long caseId,
            @RequestParam("files") MultipartFile[] files) {
        log.info("POST /{}/images - {} files", caseId, files.length);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Images processed",
                finalReportService.uploadSectionImages(caseId, files)
        ));
    }

    // ── Create ───────────────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ApiResponse<FinalReportResponse>> createReport(
            @Valid @RequestBody CreateFinalReportRequest request,
            @RequestHeader("X-Username") String createdBy) {
        log.info("POST /finalreport - caseId: {} by: {}", request.getCaseId(), createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Final report created successfully",
                finalReportService.createReport(request, createdBy)
        ));
    }

    // ── Get by ID ────────────────────────────────────────────────────────
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<FinalReportResponse>> getReportById(
            @PathVariable Long reportId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Report fetched successfully",
                finalReportService.getReportById(reportId)
        ));
    }

    // ── Get by Case ID ───────────────────────────────────────────────────
    @GetMapping("/by-case/{caseId}")
    public ResponseEntity<ApiResponse<FinalReportResponse>> getReportByCaseId(
            @PathVariable Long caseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Report fetched successfully",
                finalReportService.getReportByCaseId(caseId)
        ));
    }

    // ── List all ─────────────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FinalReportListItemResponse>>> getAllReports(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                "Reports fetched successfully",
                finalReportService.getAllReports(pageable)
        ));
    }

    // ── List by status ───────────────────────────────────────────────────
    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<Page<FinalReportListItemResponse>>> getReportsByStatus(
            @PathVariable FinalReportStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                "Reports fetched successfully",
                finalReportService.getReportsByStatus(status, pageable)
        ));
    }

    // ── Update content ───────────────────────────────────────────────────
    @PutMapping("/{reportId}")
    public ResponseEntity<ApiResponse<FinalReportResponse>> updateReport(
            @PathVariable Long reportId,
            @Valid @RequestBody UpdateFinalReportRequest request,
            @RequestHeader("X-Username") String updatedBy,
            @RequestHeader(value = "X-Admin-Edit", required = false, defaultValue = "false") boolean isAdminEdit
    ) {
        log.info("PUT /{} by: {} (adminEdit: {})", reportId, updatedBy, isAdminEdit);
        return ResponseEntity.ok(ApiResponse.success(
                "Report updated successfully",
                finalReportService.updateReport(reportId, request, updatedBy, isAdminEdit)
        ));
    }


    // ── Employee submit for approval ─────────────────────────────────────
    @PostMapping("/{reportId}/submit")
    public ResponseEntity<ApiResponse<FinalReportResponse>> submitForApproval(
            @PathVariable Long reportId,
            @RequestHeader("X-Username") String updatedBy) {
        log.info("POST /{}/submit by: {}", reportId, updatedBy);
        return ResponseEntity.ok(ApiResponse.success(
                "Report submitted for approval",
                finalReportService.submitForApproval(reportId, updatedBy)
        ));
    }

    // ── Admin: REQUEST_CHANGES or APPROVED ──────────────────────────────
    @PatchMapping("/{reportId}/status")
    public ResponseEntity<ApiResponse<FinalReportResponse>> updateStatus(
            @PathVariable Long reportId,
            @Valid @RequestBody FinalReportStatusUpdateRequest request,
            @RequestHeader("X-Username") String updatedBy) {
        log.info("PATCH /{}/status → {} by: {}", reportId, request.getReportStatus(), updatedBy);
        return ResponseEntity.ok(ApiResponse.success(
                "Report status updated successfully",
                finalReportService.updateStatus(reportId, request, updatedBy)
        ));
    }

    // ── Soft delete ──────────────────────────────────────────────────────
    @DeleteMapping("/{reportId}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @PathVariable Long reportId) {
        log.info("DELETE /{}", reportId);
        finalReportService.deleteReport(reportId);
        return ResponseEntity.ok(ApiResponse.success("Report deleted successfully", null));
    }
}
