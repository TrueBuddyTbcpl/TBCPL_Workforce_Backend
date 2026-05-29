package com.tbcpl.workforce.hr.grievance.service;

import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceRemarkRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceUpdateRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceActionRequest;
import com.tbcpl.workforce.hr.grievance.dto.response.HrGrievanceResponse;
import org.springframework.data.domain.Page;

public interface HrGrievanceService {

    /**
     * Employee raises a new grievance.
     */
    HrGrievanceResponse raiseGrievance(HrGrievanceRequest request, String createdBy);

    HrGrievanceResponse getGrievanceById(Long id, boolean includeInternal);

    HrGrievanceResponse getGrievanceByTicket(String ticketNumber, boolean includeInternal);

    Page<HrGrievanceResponse> getAllGrievances(int page, int size);

    Page<HrGrievanceResponse> getGrievancesByEmpId(String empId, int page, int size);

    Page<HrGrievanceResponse> getGrievancesByStatus(String status, int page, int size);

    Page<HrGrievanceResponse> getGrievancesByPriority(String priority, int page, int size);

    Page<HrGrievanceResponse> getGrievancesAssignedTo(String assignedTo, int page, int size);

    /**
     * HR assigns, escalates, or updates status of a grievance.
     */
    HrGrievanceResponse updateGrievance(Long id, HrGrievanceUpdateRequest request,
                                        String updatedBy);

    /**
     * Add a remark / comment to a grievance thread.
     */
    HrGrievanceResponse addRemark(Long id, HrGrievanceRemarkRequest request,
                                  String remarkedBy);

    /**
     * Resolve a grievance with resolution remarks.
     */
    HrGrievanceResponse resolveGrievance(Long id, String resolutionRemarks, String resolvedBy);

    /**
     * Targeted single-action: ASSIGN, ESCALATE, CLOSE, REJECT.
     * Lighter than the full updateGrievance() — only status transitions.
     */
    HrGrievanceResponse applyGrievanceAction(Long id, HrGrievanceActionRequest request,
                                             String actionBy);

    void deleteGrievance(Long id);
}