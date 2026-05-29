package com.tbcpl.workforce.hr.performance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.performance.dto.request.HrKraTemplateRequest;
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
public class HrKraTemplateController {

    private final HrAppraisalCycleService appraisalCycleService;

    /** POST /api/v1/hr/kras */
    @PostMapping(ApiEndpoints.HR_KRAS)
    public ResponseEntity<ApiResponse<HrKraTemplateResponse>> createKraTemplate(
            @Valid @RequestBody HrKraTemplateRequest request,
            Authentication authentication
    ) {
        log.info("Create KRA template name:{} by:{}", request.getKraName(),
                authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("KRA template created successfully",
                        appraisalCycleService.createKraTemplate(request,
                                authentication.getName())));
    }

    /**
     * GET /api/v1/hr/kras
     * Query params: designation, page, size
     */
    @GetMapping(ApiEndpoints.HR_KRAS)
    public ResponseEntity<ApiResponse<?>> getKraTemplates(
            @RequestParam(required = false)    String designation,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (designation != null) {
            List<HrKraTemplateResponse> result =
                    appraisalCycleService.getKraTemplatesByDesignation(designation);
            return ResponseEntity.ok(ApiResponse.success("KRA templates retrieved", result));
        }
        Page<HrKraTemplateResponse> result =
                appraisalCycleService.getAllKraTemplates(page, size);
        return ResponseEntity.ok(ApiResponse.success("KRA templates retrieved", result));
    }

    /** GET /api/v1/hr/kras/{id} */
    @GetMapping(ApiEndpoints.HR_KRA_BY_ID)
    public ResponseEntity<ApiResponse<HrKraTemplateResponse>> getKraTemplateById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("KRA template retrieved",
                appraisalCycleService.getKraTemplateById(id)));
    }

    /** PUT /api/v1/hr/kras/{id} */
    @PutMapping(ApiEndpoints.HR_KRA_BY_ID)
    public ResponseEntity<ApiResponse<HrKraTemplateResponse>> updateKraTemplate(
            @PathVariable Long id,
            @Valid @RequestBody HrKraTemplateRequest request,
            Authentication authentication
    ) {
        log.info("Update KRA template ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("KRA template updated successfully",
                appraisalCycleService.updateKraTemplate(id, request,
                        authentication.getName())));
    }

    /** DELETE /api/v1/hr/kras/{id} */
    @DeleteMapping(ApiEndpoints.HR_KRA_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteKraTemplate(@PathVariable Long id) {
        appraisalCycleService.deleteKraTemplate(id);
        return ResponseEntity.ok(ApiResponse.success("KRA template deleted successfully"));
    }
}