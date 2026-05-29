package com.tbcpl.workforce.hr.attendance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.attendance.dto.request.LeaveTypeRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.LeaveTypeResponse;
import com.tbcpl.workforce.hr.attendance.service.LeaveTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.HR_BASE)
@RequiredArgsConstructor
@Slf4j
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    /** POST /api/v1/hr/leave-types */
    @PostMapping(ApiEndpoints.HR_LEAVE_TYPES)
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> createLeaveType(
            @Valid @RequestBody LeaveTypeRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create leave type: {} by: {}", request.getLeaveTypeName(), createdBy);
        LeaveTypeResponse response = leaveTypeService.createLeaveType(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave type created successfully", response));
    }

    /** GET /api/v1/hr/leave-types */
    @GetMapping(ApiEndpoints.HR_LEAVE_TYPES)
    public ResponseEntity<ApiResponse<List<LeaveTypeResponse>>> getAllLeaveTypes(
            @RequestParam(defaultValue = "true") boolean activeOnly
    ) {
        log.info("Get all leave types - activeOnly: {}", activeOnly);
        List<LeaveTypeResponse> types = activeOnly
                ? leaveTypeService.getAllActiveLeaveTypes()
                : leaveTypeService.getAllLeaveTypes();
        return ResponseEntity.ok(ApiResponse.success("Leave types retrieved successfully", types));
    }

    /** GET /api/v1/hr/leave-types/{id} */
    @GetMapping(ApiEndpoints.HR_LEAVE_TYPE_BY_ID)
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> getLeaveTypeById(
            @PathVariable Long id
    ) {
        log.info("Get leave type by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("Leave type retrieved successfully",
                        leaveTypeService.getLeaveTypeById(id)));
    }

    /** PUT /api/v1/hr/leave-types/{id} */
    @PutMapping(ApiEndpoints.HR_LEAVE_TYPE_BY_ID)
    public ResponseEntity<ApiResponse<LeaveTypeResponse>> updateLeaveType(
            @PathVariable Long id,
            @Valid @RequestBody LeaveTypeRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update leave type ID: {} by: {}", id, updatedBy);
        LeaveTypeResponse response = leaveTypeService.updateLeaveType(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Leave type updated successfully", response));
    }

    /** DELETE /api/v1/hr/leave-types/{id} */
    @DeleteMapping(ApiEndpoints.HR_LEAVE_TYPE_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteLeaveType(
            @PathVariable Long id
    ) {
        log.info("Soft delete leave type ID: {}", id);
        leaveTypeService.deleteLeaveType(id);
        return ResponseEntity.ok(ApiResponse.success("Leave type deactivated successfully"));
    }
}