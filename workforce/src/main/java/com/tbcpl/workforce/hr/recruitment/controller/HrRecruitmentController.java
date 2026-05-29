package com.tbcpl.workforce.hr.recruitment.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrJobRequisitionRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrJobRequisitionResponse;
import com.tbcpl.workforce.hr.recruitment.service.HrRecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.HR_BASE)
@RequiredArgsConstructor
@Slf4j
public class HrRecruitmentController {

    private final HrRecruitmentService recruitmentService;

    /** POST /api/v1/hr/job-requisitions */
    @PostMapping(ApiEndpoints.HR_JOB_REQUISITIONS)
    public ResponseEntity<ApiResponse<HrJobRequisitionResponse>> createRequisition(
            @Valid @RequestBody HrJobRequisitionRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create job requisition: {} by: {}", request.getJobTitle(), createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job requisition created successfully",
                        recruitmentService.createRequisition(request, createdBy)));
    }

    /** GET /api/v1/hr/job-requisitions?status=OPEN&department=HR&page=0&size=20 */
    @GetMapping(ApiEndpoints.HR_JOB_REQUISITIONS)
    public ResponseEntity<ApiResponse<Page<HrJobRequisitionResponse>>> getRequisitions(
            @RequestParam(required = false)    String status,
            @RequestParam(required = false)    String department,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        log.info("Get requisitions status:{} dept:{} page:{} size:{}",
                status, department, page, size);

        Page<HrJobRequisitionResponse> result;
        if (status != null)     result = recruitmentService.getRequisitionsByStatus(status, page, size);
        else if (department != null) result = recruitmentService.getRequisitionsByDepartment(department, page, size);
        else                    result = recruitmentService.getAllRequisitions(page, size);

        return ResponseEntity.ok(ApiResponse.success("Job requisitions retrieved", result));
    }

    /** GET /api/v1/hr/job-requisitions/{id} */
    @GetMapping(ApiEndpoints.HR_JOB_REQUISITION_BY_ID)
    public ResponseEntity<ApiResponse<HrJobRequisitionResponse>> getRequisitionById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Requisition retrieved",
                recruitmentService.getRequisitionById(id)));
    }

    /** GET /api/v1/hr/job-requisitions/code/{code} */
    @GetMapping("/job-requisitions/code/{code}")
    public ResponseEntity<ApiResponse<HrJobRequisitionResponse>> getRequisitionByCode(
            @PathVariable String code
    ) {
        return ResponseEntity.ok(ApiResponse.success("Requisition retrieved",
                recruitmentService.getRequisitionByCode(code)));
    }

    /** PUT /api/v1/hr/job-requisitions/{id} */
    @PutMapping(ApiEndpoints.HR_JOB_REQUISITION_BY_ID)
    public ResponseEntity<ApiResponse<HrJobRequisitionResponse>> updateRequisition(
            @PathVariable Long id,
            @Valid @RequestBody HrJobRequisitionRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success("Requisition updated successfully",
                recruitmentService.updateRequisition(id, request, updatedBy)));
    }

    /** PATCH /api/v1/hr/job-requisitions/{id}/status?status=FILLED&remarks=All positions filled */
    @PatchMapping(ApiEndpoints.HR_JOB_REQUISITION_BY_ID + "/status")
    public ResponseEntity<ApiResponse<HrJobRequisitionResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remarks,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update requisition ID: {} status to: {} by: {}", id, status, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Requisition status updated",
                recruitmentService.updateRequisitionStatus(id, status, remarks, updatedBy)));
    }

    /** DELETE /api/v1/hr/job-requisitions/{id} */
    @DeleteMapping(ApiEndpoints.HR_JOB_REQUISITION_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteRequisition(
            @PathVariable Long id
    ) {
        recruitmentService.deleteRequisition(id);
        return ResponseEntity.ok(ApiResponse.success("Requisition deleted successfully"));
    }
}