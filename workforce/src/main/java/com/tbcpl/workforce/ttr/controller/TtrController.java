package com.tbcpl.workforce.ttr.controller;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.ttr.dto.request.TtrChildCreateRequest;
import com.tbcpl.workforce.ttr.dto.request.TtrCreateRequest;
import com.tbcpl.workforce.ttr.dto.request.TtrStatusUpdateRequest;
import com.tbcpl.workforce.ttr.dto.response.TtrCompletionRecordResponse;
import com.tbcpl.workforce.ttr.dto.response.TtrDashboardResponse;
import com.tbcpl.workforce.ttr.dto.response.TtrResponse;
import com.tbcpl.workforce.ttr.service.TtrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ttr")
@RequiredArgsConstructor
@Slf4j
public class TtrController {

    private final TtrService ttrService;

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<TtrResponse>> createParentTtr(
            @Valid @RequestBody TtrCreateRequest request,
            @RequestHeader("X-EmpId") String createdByEmpId) {

        TtrResponse response = ttrService.createParentTtr(request, createdByEmpId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("TTR created successfully", response));
    }

    @PostMapping("/{parentTtrId}/child")
    public ResponseEntity<ApiResponse<TtrResponse>> createChildTtr(
            @PathVariable Long parentTtrId,
            @Valid @RequestBody TtrChildCreateRequest request,
            @RequestHeader("X-EmpId") String createdByEmpId) {

        TtrResponse response = ttrService.createChildTtr(parentTtrId, request, createdByEmpId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Child TTR created successfully", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATUS UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    @PatchMapping("/{ttrId}/status")
    public ResponseEntity<ApiResponse<TtrResponse>> updateStatus(
            @PathVariable Long ttrId,
            @Valid @RequestBody TtrStatusUpdateRequest request,
            @RequestHeader("X-EmpId") String actorEmpId,
            @RequestHeader("X-Role")  String actorRole) {

        log.info("Status update TTR={} by empId={}", ttrId, actorEmpId);
        TtrResponse response = ttrService.updateStatus(ttrId, request, actorEmpId, actorRole);
        return ResponseEntity.ok(ApiResponse.success("TTR status updated", response));
    }

    @PostMapping(value = "/{ttrId}/status-with-proof",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<TtrResponse>> updateStatusWithProof(
            @PathVariable Long ttrId,
            @RequestPart("request") @Valid TtrStatusUpdateRequest request,
            @RequestPart(value = "proofFile", required = false) MultipartFile proofFile,
            @RequestHeader("X-EmpId") String actorEmpId,
            @RequestHeader("X-Role")  String actorRole) {

        log.info("Status update with proof TTR={} by empId={}", ttrId, actorEmpId);
        TtrResponse response = ttrService.updateStatusWithProof(
                ttrId, request, proofFile, actorEmpId, actorRole);
        return ResponseEntity.ok(ApiResponse.success("TTR status updated", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TtrResponse>> getTtrById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("TTR fetched", ttrService.getTtrById(id)));
    }

    // Single unified GET /api/v1/ttr with ttrType filter
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TtrResponse>>> getAllTtrs(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assignedEmpId,
            @RequestParam(required = false) String ttrType,
            @RequestHeader(value = "X-EmpId", required = false) String actorEmpId,
            @RequestHeader(value = "X-Role",  required = false) String actorRole) {

        Page<TtrResponse> result = ttrService.getAllTtrs(
                page, size, departmentId, status, assignedEmpId, ttrType);
        return ResponseEntity.ok(ApiResponse.success("TTRs fetched", result));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse<Page<TtrResponse>>> getTtrsByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success("Department TTRs fetched",
                ttrService.getTtrsByDepartment(departmentId, page, size)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COMPLETION HISTORY (RECURRING TTRs only)
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/{ttrId}/completion-history")
    public ResponseEntity<ApiResponse<Page<TtrCompletionRecordResponse>>> getCompletionHistory(
            @PathVariable Long ttrId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("X-EmpId") String actorEmpId,
            @RequestHeader("X-Role")  String actorRole) {

        log.info("Completion history fetch TTR={} by empId={}", ttrId, actorEmpId);
        Page<TtrCompletionRecordResponse> history =
                ttrService.getCompletionHistory(ttrId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Completion history fetched", history));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DASHBOARD
    // ─────────────────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<List<TtrDashboardResponse>>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard metrics fetched",
                ttrService.getDashboardMetrics()));
    }

    @GetMapping("/dashboard/department/{departmentId}")
    public ResponseEntity<ApiResponse<TtrDashboardResponse>> getDepartmentMetrics(
            @PathVariable Long departmentId) {
        return ResponseEntity.ok(ApiResponse.success("Department metrics fetched",
                ttrService.getDepartmentMetrics(departmentId)));
    }
}