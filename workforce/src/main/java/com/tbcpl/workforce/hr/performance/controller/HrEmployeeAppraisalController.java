package com.tbcpl.workforce.hr.performance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.performance.dto.request.*;
import com.tbcpl.workforce.hr.performance.dto.response.HrEmployeeAppraisalResponse;
import com.tbcpl.workforce.hr.performance.service.HrEmployeeAppraisalService;
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
public class HrEmployeeAppraisalController {

    private final HrEmployeeAppraisalService appraisalService;

    /** POST /api/v1/hr/appraisals/initiate?empId=2026/001&cycleId=1&managerEmpId=2025/010 */
    @PostMapping(ApiEndpoints.HR_APPRAISALS + "/initiate")
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> initiateAppraisal(
            @RequestParam String empId,
            @RequestParam Long   cycleId,
            @RequestParam(required = false) String managerEmpId,
            Authentication authentication
    ) {
        log.info("Initiate appraisal empId:{} cycleId:{} by:{}",
                empId, cycleId, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appraisal initiated successfully",
                        appraisalService.initiateAppraisal(
                                empId, cycleId, managerEmpId, authentication.getName())));
    }

    /** POST /api/v1/hr/appraisals/bulk-initiate */
    @PostMapping(ApiEndpoints.HR_APPRAISALS + "/bulk-initiate")
    public ResponseEntity<ApiResponse<String>> bulkInitiateAppraisals(
            @RequestParam Long         cycleId,
            @RequestParam(required = false) String managerEmpId,
            @RequestBody  List<String> empIds,
            Authentication authentication
    ) {
        log.info("Bulk initiate appraisals cycleId:{} count:{} by:{}",
                cycleId, empIds.size(), authentication.getName());
        int created = appraisalService.bulkInitiateAppraisals(
                cycleId, empIds, managerEmpId, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        created + " appraisal(s) initiated successfully"));
    }

    /** GET /api/v1/hr/appraisals?cycleId=1&empId=2026/001&status=SELF_REVIEW_PENDING */
    @GetMapping(ApiEndpoints.HR_APPRAISALS)
    public ResponseEntity<ApiResponse<?>> getAppraisals(
            @RequestParam(required = false)    Long   cycleId,
            @RequestParam(required = false)    String empId,
            @RequestParam(required = false)    String status,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (cycleId != null) {
            Page<HrEmployeeAppraisalResponse> result =
                    appraisalService.getAppraisalsByCycle(cycleId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Appraisals retrieved", result));
        }
        if (empId != null) {
            Page<HrEmployeeAppraisalResponse> result =
                    appraisalService.getAppraisalsByEmpId(empId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Appraisals retrieved", result));
        }
        if (status != null) {
            Page<HrEmployeeAppraisalResponse> result =
                    appraisalService.getAppraisalsByStatus(status, page, size);
            return ResponseEntity.ok(ApiResponse.success("Appraisals retrieved", result));
        }
        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Provide at least one filter: cycleId, empId or status"));
    }

    /** GET /api/v1/hr/appraisals/{id} */
    @GetMapping(ApiEndpoints.HR_APPRAISAL_BY_ID)
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> getAppraisalById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Appraisal retrieved",
                appraisalService.getAppraisalById(id)));
    }

    /** PATCH /api/v1/hr/appraisals/{id}/self-review */
    @PatchMapping(ApiEndpoints.HR_APPRAISAL_BY_ID + "/self-review")
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> submitSelfReview(
            @PathVariable Long id,
            @Valid @RequestBody HrSelfReviewRequest request,
            Authentication authentication
    ) {
        String empId = authentication.getName();
        log.info("Self review for appraisal ID:{} by empId:{}", id, empId);
        return ResponseEntity.ok(ApiResponse.success("Self review submitted successfully",
                appraisalService.submitSelfReview(id, request, empId)));
    }

    /** PATCH /api/v1/hr/appraisals/{id}/manager-review */
    @PatchMapping(ApiEndpoints.HR_APPRAISAL_BY_ID + "/manager-review")
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> submitManagerReview(
            @PathVariable Long id,
            @Valid @RequestBody HrManagerReviewRequest request,
            Authentication authentication
    ) {
        String managerEmpId = authentication.getName();
        log.info("Manager review for appraisal ID:{} by:{}", id, managerEmpId);
        return ResponseEntity.ok(ApiResponse.success("Manager review submitted successfully",
                appraisalService.submitManagerReview(id, request, managerEmpId)));
    }

    /** PATCH /api/v1/hr/appraisals/{id}/final-review */
    @PatchMapping(ApiEndpoints.HR_APPRAISAL_BY_ID + "/final-review")
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> submitFinalReview(
            @PathVariable Long id,
            @Valid @RequestBody HrFinalReviewRequest request,
            Authentication authentication
    ) {
        String hrEmpId = authentication.getName();
        log.info("HR final review for appraisal ID:{} by:{}", id, hrEmpId);
        return ResponseEntity.ok(ApiResponse.success("Appraisal finalised successfully",
                appraisalService.submitFinalReview(id, request, hrEmpId)));
    }

    /** DELETE /api/v1/hr/appraisals/{id} */
    @DeleteMapping(ApiEndpoints.HR_APPRAISAL_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteAppraisal(@PathVariable Long id) {
        appraisalService.deleteAppraisal(id);
        return ResponseEntity.ok(ApiResponse.success("Appraisal deleted successfully"));
    }
}