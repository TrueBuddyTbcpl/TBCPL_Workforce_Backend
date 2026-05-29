package com.tbcpl.workforce.hr.payroll.service;

import com.tbcpl.workforce.hr.payroll.dto.request.HrPayrollInputRequest;
import com.tbcpl.workforce.hr.payroll.dto.response.HrPayrollInputResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HrPayrollInputService {

    HrPayrollInputResponse addPayrollInput(HrPayrollInputRequest request, String createdBy);

    HrPayrollInputResponse getPayrollInputById(Long id);


    List<HrPayrollInputResponse> getPayrollInputsByEmpAndMonth(
            String empId, Integer month, Integer year);

    Page<HrPayrollInputResponse> getPayrollInputsByMonth(
            Integer month, Integer year, int page, int size);

    Page<HrPayrollInputResponse> getPayrollInputsByEmpId(
            String empId, int page, int size);

    HrPayrollInputResponse updatePayrollInput(Long id, HrPayrollInputRequest request,
                                              String updatedBy);

    /**
     * Submit all DRAFT inputs for a given month to Accounts.
     * Changes status from DRAFT → SUBMITTED.
     */
    int submitPayrollInputsForMonth(Integer month, Integer year, String submittedBy);

    void deletePayrollInput(Long id);
}