package com.tbcpl.workforce.hr.performance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.performance.dto.request.HrAppraisalCycleRequest;
import com.tbcpl.workforce.hr.performance.dto.request.HrKraTemplateRequest;
import com.tbcpl.workforce.hr.performance.dto.response.HrAppraisalCycleResponse;
import com.tbcpl.workforce.hr.performance.dto.response.HrKraTemplateResponse;
import com.tbcpl.workforce.hr.performance.service.HrAppraisalCycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.HR_BASE)
@RequiredArgsConstructor
@Slf4j
public class HrAppraisalCycleController {

    private final HrAppraisalCycleService cycleService;

    // ── Appraisal Cycles ──────────────────────────────────────────────────────

    /** POST /api/v1/hr/appraisal-cycles */
    @PostMapping(ApiEndpoints.HR_APPRAISAL_CYCLES)
    public ResponseEntity<ApiResponse<HrAppraisalCycleResponse>> createCycle(
            @Valid @RequestBody HrAppraisalCycleRequest request,
            Authentication authentication
    ) {
        log.info("Create appraisal cycle: {} by:{}", request.getCycleName(),
                authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appraisal cycle created successfully",
                        cycleService.createCycle(request, authentication.getName())));
    }

    /** GET /api/v1/hr/appraisal-cycles?status=DRAFT&year=2026&page=0&size=20 */
    @GetMapping(ApiEndpoints.HR_APPRAISAL_CYCLES)
    public ResponseEntity<ApiResponse<?>> getCycles(
            @RequestParam(required = false)    String  status,
            @RequestParam(required = false)    Integer year,
            @RequestParam(defaultValue = "0")  int     page,
            @RequestParam(defaultValue = "20") int     size
    ) {
        if (year != null) {
            List<HrAppraisalCycleResponse> result = cycleService.getCyclesByYear(year);
            return ResponseEntity.ok(ApiResponse.success("Cycles retrieved", result));
        }
        if (status != null) {
            Page<HrAppraisalCycleResponse> result =
                    cycleService.getCyclesByStatus(status, page, size);
            return ResponseEntity.ok(ApiResponse.success("Cycles retrieved", result));
        }
        Page<HrAppraisalCycleResponse> result = cycleService.getAllCycles(page, size);
        return ResponseEntity.ok(ApiResponse.success("Cycles retrieved", result));
    }

    /** GET /api/v1/hr/appraisal-cycles/{id} */
    @GetMapping(ApiEndpoints.HR_APPRAISAL_CYCLE_BY_ID)
    public ResponseEntity<ApiResponse<HrAppraisalCycleResponse>> getCycleById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cycle retrieved",
                cycleService.getCycleById(id)));
    }

    /** PUT /api/v1/hr/appraisal-cycles/{id} */
    @PutMapping(ApiEndpoints.HR_APPRAISAL_CYCLE_BY_ID)
    public ResponseEntity<ApiResponse<HrAppraisalCycleResponse>> updateCycle(
            @PathVariable Long id,
            @Valid @RequestBody HrAppraisalCycleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("Cycle updated successfully",
                cycleService.updateCycle(id, request, authentication.getName())));
    }

    /** PATCH /api/v1/hr/appraisal-cycles/{id}/status?status=SELF_REVIEW_PENDING */
    @PatchMapping(ApiEndpoints.HR_APPRAISAL_CYCLE_BY_ID + "/status")
    public ResponseEntity<ApiResponse<HrAppraisalCycleResponse>> updateCycleStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication
    ) {
        log.info("Update cycle ID:{} status:{} by:{}", id, status, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Cycle status updated",
                cycleService.updateCycleStatus(id, status, authentication.getName())));
    }

    /** DELETE /api/v1/hr/appraisal-cycles/{id} */
    @DeleteMapping(ApiEndpoints.HR_APPRAISAL_CYCLE_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteCycle(@PathVariable Long id) {
        cycleService.deleteCycle(id);
        return ResponseEntity.ok(ApiResponse.success("Cycle deleted successfully"));
    }

    // ── KRA Templates ─────────────────────────────────────────────────────────

    /** POST /api/v1/hr/kra-templates */
    @PostMapping(ApiEndpoints.HR_KRA_TEMPLATES)
    public ResponseEntity<ApiResponse<HrKraTemplateResponse>> createKraTemplate(
            @Valid @RequestBody HrKraTemplateRequest request,
            Authentication authentication
    ) {
        log.info("Create KRA template: {} by:{}", request.getKraName(),
                authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("KRA template created successfully",
                        cycleService.createKraTemplate(request, authentication.getName())));
    }

    /** GET /api/v1/hr/kra-templates?designation=Software+Engineer&page=0&size=20 */
    @GetMapping(ApiEndpoints.HR_KRA_TEMPLATES)
    public ResponseEntity<ApiResponse<?>> getKraTemplates(
            @RequestParam(required = false)    String designation,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (designation != null) {
            List<HrKraTemplateResponse> result =
                    cycleService.getKraTemplatesByDesignation(designation);
            return ResponseEntity.ok(ApiResponse.success("KRA templates retrieved", result));
        }
        Page<HrKraTemplateResponse> result = cycleService.getAllKraTemplates(page, size);
        return ResponseEntity.ok(ApiResponse.success("KRA templates retrieved", result));
    }

    /** GET /api/v1/hr/kra-templates/{id} */
    @GetMapping(ApiEndpoints.HR_KRA_TEMPLATE_BY_ID)
    public ResponseEntity<ApiResponse<HrKraTemplateResponse>> getKraTemplateById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("KRA template retrieved",
                cycleService.getKraTemplateById(id)));
    }

    /** PUT /api/v1/hr/kra-templates/{id} */
    @PutMapping(ApiEndpoints.HR_KRA_TEMPLATE_BY_ID)
    public ResponseEntity<ApiResponse<HrKraTemplateResponse>> updateKraTemplate(
            @PathVariable Long id,
            @Valid @RequestBody HrKraTemplateRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("KRA template updated successfully",
                cycleService.updateKraTemplate(id, request, authentication.getName())));
    }

    /** DELETE /api/v1/hr/kra-templates/{id} */
    @DeleteMapping(ApiEndpoints.HR_KRA_TEMPLATE_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteKraTemplate(@PathVariable Long id) {
        cycleService.deleteKraTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("KRA template deleted successfully"));
    }
}