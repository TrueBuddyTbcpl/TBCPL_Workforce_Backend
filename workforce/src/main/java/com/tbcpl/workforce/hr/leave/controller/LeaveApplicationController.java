package com.tbcpl.workforce.hr.leave.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.leave.dto.request.LeaveActionRequest;
import com.tbcpl.workforce.hr.leave.dto.request.LeaveApplicationRequest;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveApplicationResponse;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveBalanceResponse;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveBalanceSummaryResponse;
import com.tbcpl.workforce.hr.leave.service.LeaveService;
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
public class LeaveApplicationController {

    private final LeaveService leaveService;

    // ── Leave Balance ─────────────────────────────────────────────────────────


    @PostMapping("/leave-balance/initialize")
    public ResponseEntity<ApiResponse<LeaveBalanceResponse>> initializeLeaveBalance(
            @RequestParam String empId,
            @RequestParam Long   leaveTypeId,
            @RequestParam(defaultValue = "0") Integer year,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        int targetYear   = year == 0 ? java.time.LocalDate.now().getYear() : year;
        log.info("Initialize leave balance for empId:{} leaveTypeId:{} year:{} by:{}",
                empId, leaveTypeId, targetYear, createdBy);
        LeaveBalanceResponse response =
                leaveService.initializeLeaveBalance(empId, leaveTypeId, targetYear, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave balance initialized successfully", response));
    }

    /**
     * GET /api/v1/hr/leave-balance/emp/{empId}?year=2026
     * Get leave balance summary for an employee
     */
    @GetMapping(ApiEndpoints.HR_LEAVE_BALANCE_BY_EMP)
    public ResponseEntity<ApiResponse<LeaveBalanceSummaryResponse>> getLeaveBalanceSummary(
            @PathVariable String empId,
            @RequestParam(required = false) Integer year
    ) {
        log.info("Get leave balance summary for empId:{} year:{}", empId, year);
        LeaveBalanceSummaryResponse summary = leaveService.getLeaveBalanceSummary(empId, year);
        return ResponseEntity.ok(
                ApiResponse.success("Leave balance summary retrieved", summary));
    }

    /**
     * GET /api/v1/hr/leave-balance/emp/{empId}/all
     * Get all leave balances for an employee across years
     */
    @GetMapping("/leave-balance/emp/{empId}/all")
    public ResponseEntity<ApiResponse<List<LeaveBalanceResponse>>> getAllLeaveBalances(
            @PathVariable String empId
    ) {
        log.info("Get all leave balances for empId: {}", empId);
        List<LeaveBalanceResponse> balances = leaveService.getLeaveBalances(empId, null);
        return ResponseEntity.ok(
                ApiResponse.success("Leave balances retrieved", balances));
    }

    /**
     * POST /api/v1/hr/leave-balance/carry-forward?year=2025
     * Year-end carry-forward processing — HR/Admin only
     */
    @PostMapping("/leave-balance/carry-forward")
    public ResponseEntity<ApiResponse<Void>> processCarryForward(
            @RequestParam Integer year,
            Authentication authentication
    ) {
        String processedBy = authentication.getName();
        log.info("Process carry-forward for year: {} by: {}", year, processedBy);
        leaveService.processYearEndCarryForward(year, processedBy);
        return ResponseEntity.ok(
                ApiResponse.success("Carry-forward processed successfully for year: " + year));
    }

    // ── Leave Application ─────────────────────────────────────────────────────

    /**
     * POST /api/v1/hr/leave-applications
     * Apply for leave
     */
    @PostMapping(ApiEndpoints.HR_LEAVE_APPLICATIONS)
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> applyLeave(
            @Valid @RequestBody LeaveApplicationRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Leave application by empId: {} from: {} to: {}",
                request.getEmpId(), request.getFromDate(), request.getToDate());
        LeaveApplicationResponse response = leaveService.applyLeave(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Leave application submitted successfully", response));
    }

    /**
     * GET /api/v1/hr/leave-applications/{id}
     */
    @GetMapping(ApiEndpoints.HR_LEAVE_APPLICATION_BY_ID)
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> getLeaveApplicationById(
            @PathVariable Long id
    ) {
        log.info("Get leave application by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("Leave application retrieved",
                        leaveService.getLeaveApplicationById(id)));
    }

    /**
     * GET /api/v1/hr/leave-applications?empId=2026/001&page=0&size=10
     * Get leave applications — filter by empId (optional)
     */
    @GetMapping(ApiEndpoints.HR_LEAVE_APPLICATIONS)
    public ResponseEntity<ApiResponse<Page<LeaveApplicationResponse>>> getLeaveApplications(
            @RequestParam(required = false)    String empId,
            @RequestParam(defaultValue = "false") boolean pendingOnly,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Get leave applications - empId:{} pendingOnly:{} page:{} size:{}",
                empId, pendingOnly, page, size);

        Page<LeaveApplicationResponse> applications;

        if (empId != null && !empId.isBlank()) {
            applications = leaveService.getLeaveApplicationsByEmpId(empId, page, size);
        } else if (pendingOnly) {
            applications = leaveService.getPendingApplications(page, size);
        } else {
            applications = leaveService.getAllLeaveApplications(page, size);
        }

        return ResponseEntity.ok(
                ApiResponse.success("Leave applications retrieved", applications));
    }

    /**
     * PATCH /api/v1/hr/leave-applications/{id}/action
     * Approve / Reject / Revoke a leave application
     */
    @PatchMapping(ApiEndpoints.HR_LEAVE_APPLICATION_ACTION)
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> processLeaveAction(
            @PathVariable Long id,
            @Valid @RequestBody LeaveActionRequest request,
            Authentication authentication
    ) {
        String reviewedBy = authentication.getName();
        log.info("Leave action: {} for ID: {} by: {}", request.getAction(), id, reviewedBy);
        LeaveApplicationResponse response =
                leaveService.processLeaveAction(id, request, reviewedBy);
        return ResponseEntity.ok(
                ApiResponse.success("Leave application " + request.getAction().name().toLowerCase()
                        + " successfully", response));
    }


    @PatchMapping(ApiEndpoints.HR_LEAVE_APPLICATION_BY_ID + "/cancel")
    public ResponseEntity<ApiResponse<LeaveApplicationResponse>> cancelLeaveApplication(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String cancelledBy = authentication.getName();
        log.info("Cancel leave application ID: {} by: {}", id, cancelledBy);
        LeaveApplicationResponse response =
                leaveService.cancelLeaveApplication(id, cancelledBy);
        return ResponseEntity.ok(
                ApiResponse.success("Leave application cancelled successfully", response));
    }
}