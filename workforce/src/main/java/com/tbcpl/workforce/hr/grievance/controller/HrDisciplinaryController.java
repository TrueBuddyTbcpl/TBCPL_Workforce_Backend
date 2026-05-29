package com.tbcpl.workforce.hr.grievance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.grievance.dto.request.HrDisciplinaryActionRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrDisciplinaryStatusRequest;
import com.tbcpl.workforce.hr.grievance.dto.response.HrDisciplinaryActionResponse;
import com.tbcpl.workforce.hr.grievance.service.HrDisciplinaryService;
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
public class HrDisciplinaryController {

    private final HrDisciplinaryService disciplinaryService;

    /** POST /api/v1/hr/disciplinary-actions */
    @PostMapping(ApiEndpoints.HR_DISCIPLINARY_ACTIONS)
    public ResponseEntity<ApiResponse<HrDisciplinaryActionResponse>> initiateAction(
            @Valid @RequestBody HrDisciplinaryActionRequest request,
            Authentication authentication
    ) {
        log.info("Initiate disciplinary action empId:{} type:{} by:{}",
                request.getEmpId(), request.getActionType(), authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Disciplinary action initiated successfully",
                        disciplinaryService.initiateAction(request, authentication.getName())));
    }

    /**
     * GET /api/v1/hr/disciplinary-actions
     * Query params: empId, status, actionType, page, size
     */
    @GetMapping(ApiEndpoints.HR_DISCIPLINARY_ACTIONS)
    public ResponseEntity<ApiResponse<?>> getActions(
            @RequestParam(required = false)    String empId,
            @RequestParam(required = false)    String status,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (empId != null) {
            Page<HrDisciplinaryActionResponse> result =
                    disciplinaryService.getActionsByEmpId(empId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Actions retrieved", result));
        }
        if (status != null) {
            Page<HrDisciplinaryActionResponse> result =
                    disciplinaryService.getActionsByStatus(status, page, size);
            return ResponseEntity.ok(ApiResponse.success("Actions retrieved", result));
        }
        Page<HrDisciplinaryActionResponse> result =
                disciplinaryService.getAllActions(page, size);
        return ResponseEntity.ok(ApiResponse.success("Actions retrieved", result));
    }

    /** GET /api/v1/hr/disciplinary-actions/{id} */
    @GetMapping(ApiEndpoints.HR_DISCIPLINARY_ACTION_BY_ID)
    public ResponseEntity<ApiResponse<HrDisciplinaryActionResponse>> getActionById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Action retrieved",
                disciplinaryService.getActionById(id)));
    }

    /** GET /api/v1/hr/disciplinary-actions/ref/{caseReference} */
    @GetMapping("/disciplinary-actions/ref/{caseReference}")
    public ResponseEntity<ApiResponse<HrDisciplinaryActionResponse>> getActionByCaseRef(
            @PathVariable String caseReference
    ) {
        return ResponseEntity.ok(ApiResponse.success("Action retrieved",
                disciplinaryService.getActionByCaseReference(caseReference)));
    }

    /** GET /api/v1/hr/disciplinary-actions/history/{empId} */
    @GetMapping("/disciplinary-actions/history/{empId}")
    public ResponseEntity<ApiResponse<List<HrDisciplinaryActionResponse>>> getHistory(
            @PathVariable String empId
    ) {
        log.info("Fetch disciplinary history for empId:{}", empId);
        return ResponseEntity.ok(ApiResponse.success("Disciplinary history retrieved",
                disciplinaryService.getEmpDisciplinaryHistory(empId)));
    }

    /** PUT /api/v1/hr/disciplinary-actions/{id} */
    @PutMapping(ApiEndpoints.HR_DISCIPLINARY_ACTION_BY_ID)
    public ResponseEntity<ApiResponse<HrDisciplinaryActionResponse>> updateAction(
            @PathVariable Long id,
            @Valid @RequestBody HrDisciplinaryActionRequest request,
            Authentication authentication
    ) {
        log.info("Update disciplinary action ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Action updated successfully",
                disciplinaryService.updateAction(id, request, authentication.getName())));
    }

    /** PATCH /api/v1/hr/disciplinary-actions/{id}/status */
    @PatchMapping(ApiEndpoints.HR_DISCIPLINARY_ACTION_BY_ID + "/status")
    public ResponseEntity<ApiResponse<HrDisciplinaryActionResponse>> updateActionStatus(
            @PathVariable Long id,
            @Valid @RequestBody HrDisciplinaryStatusRequest request,
            Authentication authentication
    ) {
        log.info("Update disciplinary status ID:{} to:{} by:{}",
                id, request.getStatus(), authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Action status updated",
                disciplinaryService.updateActionStatus(id, request, authentication.getName())));
    }

    /** DELETE /api/v1/hr/disciplinary-actions/{id} */
    @DeleteMapping(ApiEndpoints.HR_DISCIPLINARY_ACTION_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteAction(@PathVariable Long id) {
        disciplinaryService.deleteAction(id);
        return ResponseEntity.ok(ApiResponse.success("Action deleted successfully"));
    }
}