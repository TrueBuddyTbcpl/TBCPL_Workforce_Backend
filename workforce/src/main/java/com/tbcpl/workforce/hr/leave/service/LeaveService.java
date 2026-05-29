package com.tbcpl.workforce.hr.leave.service;

import com.tbcpl.workforce.hr.leave.dto.request.LeaveActionRequest;
import com.tbcpl.workforce.hr.leave.dto.request.LeaveApplicationRequest;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveApplicationResponse;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveBalanceResponse;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveBalanceSummaryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LeaveService {

    // ── Leave Balance ─────────────────────────────────────────────────────────

    /**
     * Initialize leave balance for an employee for a given year.
     * Reads maxDaysPerYear from LeaveType.
     * Called when a new employee is onboarded or at year start.
     */
    LeaveBalanceResponse initializeLeaveBalance(String empId, Long leaveTypeId,
                                                Integer year, String createdBy);

    /**
     * Get complete leave balance summary for an employee for a year.
     */
    LeaveBalanceSummaryResponse getLeaveBalanceSummary(String empId, Integer year);

    /**
     * Get all leave balances for an employee.
     */
    List<LeaveBalanceResponse> getLeaveBalances(String empId, Integer year);

    /**
     * Year-end carry-forward: process all eligible balances from the given year.
     * Carries forward min(availableBalance, maxCarryForwardDays) to next year.
     */
    void processYearEndCarryForward(Integer year, String processedBy);

    // ── Leave Application ─────────────────────────────────────────────────────

    /**
     * Apply for leave. Validates balance, overlap, notice period.
     */
    LeaveApplicationResponse applyLeave(LeaveApplicationRequest request, String createdBy);

    /**
     * Get single leave application by ID.
     */
    LeaveApplicationResponse getLeaveApplicationById(Long id);

    /**
     * Get all leave applications for an employee (paginated).
     */
    Page<LeaveApplicationResponse> getLeaveApplicationsByEmpId(
            String empId, int page, int size);

    /**
     * Get all pending applications across all employees (HR view).
     */
    Page<LeaveApplicationResponse> getPendingApplications(int page, int size);

    /**
     * Get all leave applications (HR view, paginated).
     */
    Page<LeaveApplicationResponse> getAllLeaveApplications(int page, int size);

    /**
     * Approve or Reject a leave application.
     * Updates leave balance accordingly.
     */
    LeaveApplicationResponse processLeaveAction(Long id, LeaveActionRequest request,
                                                String reviewedBy);

    /**
     * Cancel a PENDING leave application (by the employee themselves).
     */
    LeaveApplicationResponse cancelLeaveApplication(Long id, String cancelledBy);
}