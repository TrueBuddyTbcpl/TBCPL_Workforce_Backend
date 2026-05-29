package com.tbcpl.workforce.hr.grievance.service;

import com.tbcpl.workforce.hr.grievance.dto.request.HrDisciplinaryActionRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrDisciplinaryStatusRequest;
import com.tbcpl.workforce.hr.grievance.dto.response.HrDisciplinaryActionResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HrDisciplinaryService {

    /**
     * Initiate a disciplinary action against an employee.
     */
    HrDisciplinaryActionResponse initiateAction(HrDisciplinaryActionRequest request,
                                                String initiatedBy);

    HrDisciplinaryActionResponse getActionById(Long id);

    HrDisciplinaryActionResponse getActionByCaseReference(String caseReference);

    Page<HrDisciplinaryActionResponse> getAllActions(int page, int size);

    Page<HrDisciplinaryActionResponse> getActionsByEmpId(String empId, int page, int size);

    Page<HrDisciplinaryActionResponse> getActionsByStatus(String status, int page, int size);

    /**
     * Get full disciplinary history for an employee.
     */
    List<HrDisciplinaryActionResponse> getEmpDisciplinaryHistory(String empId);

    /**
     * Update disciplinary case — add employee response, notice details, final decision.
     */
    HrDisciplinaryActionResponse updateAction(Long id, HrDisciplinaryActionRequest request,
                                              String updatedBy);

    /**
     * Progress the disciplinary case status.
     */
    HrDisciplinaryActionResponse updateActionStatus(Long id,
                                                    HrDisciplinaryStatusRequest request,
                                                    String updatedBy);

    void deleteAction(Long id);
}