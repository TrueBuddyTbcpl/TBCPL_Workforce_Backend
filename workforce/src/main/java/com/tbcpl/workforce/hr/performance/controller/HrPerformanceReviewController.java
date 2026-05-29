package com.tbcpl.workforce.hr.performance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.performance.dto.request.HrFinalReviewRequest;
import com.tbcpl.workforce.hr.performance.dto.request.HrManagerReviewRequest;
import com.tbcpl.workforce.hr.performance.dto.request.HrSelfReviewRequest;
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
public class HrPerformanceReviewController {

    private final HrEmployeeAppraisalService appraisalService;

    /**
     * POST /api/v1/hr/performance-reviews
     * HR initiates a single appraisal for an employee in a cycle.
     * Body params: empId, cycleId, managerEmpId
     */
    @PostMapping(ApiEndpoints.HR_PERFORMANCE_REVIEWS)
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
                        appraisalService.initiateAppraisal(empId, cycleId,
                                managerEmpId, authentication.getName())));
    }

    /**
     * POST /api/v1/hr/performance-reviews/bulk
     * HR bulk-initiates appraisals for a list of employees in a cycle.
     */
    @PostMapping(ApiEndpoints.HR_PERFORMANCE_REVIEWS + "/bulk")
    public ResponseEntity<ApiResponse<String>> bulkInitiateAppraisals(
            @RequestParam Long         cycleId,
            @RequestParam(required = false) String managerEmpId,
            @RequestBody  List<String> empIds,
            Authentication authentication
    ) {
        log.info("Bulk initiate appraisals cycleId:{} count:{} by:{}",
                cycleId, empIds.size(), authentication.getName());
        int count = appraisalService.bulkInitiateAppraisals(
                cycleId, empIds, managerEmpId, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(count + " appraisal(s) initiated successfully",
                        count + " records created"));
    }

    /**
     * GET /api/v1/hr/performance-reviews
     * Query params: empId, cycleId, status, page, size
     */
    @GetMapping(ApiEndpoints.HR_PERFORMANCE_REVIEWS)
    public ResponseEntity<ApiResponse<?>> getAppraisals(
            @RequestParam(required = false)    String empId,
            @RequestParam(required = false)    Long   cycleId,
            @RequestParam(required = false)    String status,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (empId != null) {
            Page<HrEmployeeAppraisalResponse> result =
                    appraisalService.getAppraisalsByEmpId(empId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Appraisals retrieved", result));
        }
        if (cycleId != null) {
            Page<HrEmployeeAppraisalResponse> result =
                    appraisalService.getAppraisalsByCycle(cycleId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Appraisals retrieved", result));
        }
        if (status != null) {
            Page<HrEmployeeAppraisalResponse> result =
                    appraisalService.getAppraisalsByStatus(status, page, size);
            return ResponseEntity.ok(ApiResponse.success("Appraisals retrieved", result));
        }
        return ResponseEntity.ok(ApiResponse.success("Please provide empId, cycleId, or status",
                null));
    }

    /** GET /api/v1/hr/performance-reviews/{id} */
    @GetMapping(ApiEndpoints.HR_PERFORMANCE_REVIEW_BY_ID)
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> getAppraisalById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Appraisal retrieved",
                appraisalService.getAppraisalById(id)));
    }

    /** PATCH /api/v1/hr/performance-reviews/{id}/self-review */
    @PatchMapping(ApiEndpoints.HR_PERFORMANCE_REVIEW_BY_ID + "/self-review")
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> submitSelfReview(
            @PathVariable Long id,
            @Valid @RequestBody HrSelfReviewRequest request,
            Authentication authentication
    ) {
        log.info("Submit self review appraisalId:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Self review submitted successfully",
                appraisalService.submitSelfReview(id, request, authentication.getName())));
    }

    /** PATCH /api/v1/hr/performance-reviews/{id}/manager-review */
    @PatchMapping(ApiEndpoints.HR_PERFORMANCE_REVIEW_BY_ID + "/manager-review")
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> submitManagerReview(
            @PathVariable Long id,
            @Valid @RequestBody HrManagerReviewRequest request,
            Authentication authentication
    ) {
        log.info("Submit manager review appraisalId:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Manager review submitted successfully",
                appraisalService.submitManagerReview(id, request, authentication.getName())));
    }

    /** PATCH /api/v1/hr/performance-reviews/{id}/final-review */
    @PatchMapping(ApiEndpoints.HR_PERFORMANCE_REVIEW_BY_ID + "/final-review")
    public ResponseEntity<ApiResponse<HrEmployeeAppraisalResponse>> submitFinalReview(
            @PathVariable Long id,
            @Valid @RequestBody HrFinalReviewRequest request,
            Authentication authentication
    ) {
        log.info("Submit final HR review appraisalId:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Final review submitted and locked",
                appraisalService.submitFinalReview(id, request, authentication.getName())));
    }

    /** DELETE /api/v1/hr/performance-reviews/{id} */
    @DeleteMapping(ApiEndpoints.HR_PERFORMANCE_REVIEW_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteAppraisal(@PathVariable Long id) {
        appraisalService.deleteAppraisal(id);
        return ResponseEntity.ok(ApiResponse.success("Appraisal deleted successfully"));
    }
}