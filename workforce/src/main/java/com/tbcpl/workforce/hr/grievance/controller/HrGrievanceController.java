package com.tbcpl.workforce.hr.grievance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceActionRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceRemarkRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceUpdateRequest;
import com.tbcpl.workforce.hr.grievance.dto.response.HrGrievanceResponse;
import com.tbcpl.workforce.hr.grievance.service.HrGrievanceService;
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
public class HrGrievanceController {

    private final HrGrievanceService grievanceService;

    /** POST /api/v1/hr/grievances */
    @PostMapping(ApiEndpoints.HR_GRIEVANCES)
    public ResponseEntity<ApiResponse<HrGrievanceResponse>> raiseGrievance(
            @Valid @RequestBody HrGrievanceRequest request,
            Authentication authentication
    ) {
        log.info("Raise grievance empId:{} by:{}", request.getEmpId(),
                authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Grievance raised successfully",
                        grievanceService.raiseGrievance(request, authentication.getName())));
    }

    /**
     * GET /api/v1/hr/grievances
     * Query params: empId, status, priority, assignedTo, page, size
     */
    @GetMapping(ApiEndpoints.HR_GRIEVANCES)
    public ResponseEntity<ApiResponse<?>> getGrievances(
            @RequestParam(required = false)    String empId,
            @RequestParam(required = false)    String status,
            @RequestParam(required = false)    String priority,
            @RequestParam(required = false)    String assignedTo,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (empId != null) {
            Page<HrGrievanceResponse> result =
                    grievanceService.getGrievancesByEmpId(empId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Grievances retrieved", result));
        }
        if (status != null) {
            Page<HrGrievanceResponse> result =
                    grievanceService.getGrievancesByStatus(status, page, size);
            return ResponseEntity.ok(ApiResponse.success("Grievances retrieved", result));
        }
        if (priority != null) {
            Page<HrGrievanceResponse> result =
                    grievanceService.getGrievancesByPriority(priority, page, size);
            return ResponseEntity.ok(ApiResponse.success("Grievances retrieved", result));
        }
        if (assignedTo != null) {
            Page<HrGrievanceResponse> result =
                    grievanceService.getGrievancesAssignedTo(assignedTo, page, size);
            return ResponseEntity.ok(ApiResponse.success("Grievances retrieved", result));
        }
        Page<HrGrievanceResponse> result = grievanceService.getAllGrievances(page, size);
        return ResponseEntity.ok(ApiResponse.success("Grievances retrieved", result));
    }

    /** GET /api/v1/hr/grievances/{id}?internal=true */
    @GetMapping(ApiEndpoints.HR_GRIEVANCE_BY_ID)
    public ResponseEntity<ApiResponse<HrGrievanceResponse>> getGrievanceById(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean internal
    ) {
        return ResponseEntity.ok(ApiResponse.success("Grievance retrieved",
                grievanceService.getGrievanceById(id, internal)));
    }

    /** GET /api/v1/hr/grievances/ticket/{ticketNumber} */
    @GetMapping("/grievances/ticket/{ticketNumber}")
    public ResponseEntity<ApiResponse<HrGrievanceResponse>> getByTicket(
            @PathVariable String ticketNumber,
            @RequestParam(defaultValue = "false") boolean internal
    ) {
        return ResponseEntity.ok(ApiResponse.success("Grievance retrieved",
                grievanceService.getGrievanceByTicket(ticketNumber, internal)));
    }

    /**
     * PATCH /api/v1/hr/grievances/{id}/action
     * Targeted action: ASSIGN → UNDER_REVIEW, ESCALATE, CLOSE, REJECT
     */
    @PatchMapping(ApiEndpoints.HR_GRIEVANCE_ACTION)
    public ResponseEntity<ApiResponse<HrGrievanceResponse>> applyGrievanceAction(
            @PathVariable Long id,
            @Valid @RequestBody HrGrievanceActionRequest request,
            Authentication authentication
    ) {
        log.info("Grievance action ID:{} status:{} by:{}",
                id, request.getStatus(), authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Grievance action applied successfully",
                grievanceService.applyGrievanceAction(id, request, authentication.getName())));
    }

    /** PUT /api/v1/hr/grievances/{id} */
    @PutMapping(ApiEndpoints.HR_GRIEVANCE_BY_ID)
    public ResponseEntity<ApiResponse<HrGrievanceResponse>> updateGrievance(
            @PathVariable Long id,
            @Valid @RequestBody HrGrievanceUpdateRequest request,
            Authentication authentication
    ) {
        log.info("Update grievance ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Grievance updated successfully",
                grievanceService.updateGrievance(id, request, authentication.getName())));
    }

    /** POST /api/v1/hr/grievances/{id}/remarks */
    @PostMapping(ApiEndpoints.HR_GRIEVANCE_BY_ID + "/remarks")
    public ResponseEntity<ApiResponse<HrGrievanceResponse>> addRemark(
            @PathVariable Long id,
            @Valid @RequestBody HrGrievanceRemarkRequest request,
            Authentication authentication
    ) {
        log.info("Add remark to grievance ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Remark added successfully",
                        grievanceService.addRemark(id, request, authentication.getName())));
    }

    /** PATCH /api/v1/hr/grievances/{id}/resolve */
    @PatchMapping(ApiEndpoints.HR_GRIEVANCE_BY_ID + "/resolve")
    public ResponseEntity<ApiResponse<HrGrievanceResponse>> resolveGrievance(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks,
            Authentication authentication
    ) {
        log.info("Resolve grievance ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Grievance resolved successfully",
                grievanceService.resolveGrievance(id, remarks, authentication.getName())));
    }

    /** DELETE /api/v1/hr/grievances/{id} */
    @DeleteMapping(ApiEndpoints.HR_GRIEVANCE_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteGrievance(@PathVariable Long id) {
        grievanceService.deleteGrievance(id);
        return ResponseEntity.ok(ApiResponse.success("Grievance deleted successfully"));
    }
}