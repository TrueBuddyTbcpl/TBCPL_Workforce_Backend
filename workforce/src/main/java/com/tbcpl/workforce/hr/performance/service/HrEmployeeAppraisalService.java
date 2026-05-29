package com.tbcpl.workforce.hr.performance.service;

import com.tbcpl.workforce.hr.performance.dto.request.HrFinalReviewRequest;
import com.tbcpl.workforce.hr.performance.dto.request.HrManagerReviewRequest;
import com.tbcpl.workforce.hr.performance.dto.request.HrSelfReviewRequest;
import com.tbcpl.workforce.hr.performance.dto.response.HrEmployeeAppraisalResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HrEmployeeAppraisalService {

    /**
     * HR initiates appraisal for a specific employee in a cycle.
     */
    HrEmployeeAppraisalResponse initiateAppraisal(String empId, Long cycleId,
                                                  String managerEmpId, String createdBy);

    /**
     * HR bulk-initiates appraisals for all employees in a cycle.
     * Returns count of appraisals created.
     */
    int bulkInitiateAppraisals(Long cycleId, List<String> empIds,
                               String managerEmpId, String createdBy);

    HrEmployeeAppraisalResponse getAppraisalById(Long id);

    HrEmployeeAppraisalResponse getAppraisalByEmpAndCycle(String empId, Long cycleId);

    Page<HrEmployeeAppraisalResponse> getAppraisalsByCycle(Long cycleId, int page, int size);

    Page<HrEmployeeAppraisalResponse> getAppraisalsByEmpId(String empId, int page, int size);

    Page<HrEmployeeAppraisalResponse> getAppraisalsByStatus(String status, int page, int size);

    /**
     * Employee submits self-review with KRA ratings.
     */
    HrEmployeeAppraisalResponse submitSelfReview(Long id, HrSelfReviewRequest request,
                                                 String empId);

    /**
     * Manager submits review with KRA ratings.
     * Computes weighted score automatically.
     */
    HrEmployeeAppraisalResponse submitManagerReview(Long id, HrManagerReviewRequest request,
                                                    String managerEmpId);

    /**
     * HR finalises the appraisal and locks it with final rating + increment.
     */
    HrEmployeeAppraisalResponse submitFinalReview(Long id, HrFinalReviewRequest request,
                                                  String hrEmpId);

    void deleteAppraisal(Long id);
}