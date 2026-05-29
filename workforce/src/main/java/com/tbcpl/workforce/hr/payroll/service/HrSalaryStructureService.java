package com.tbcpl.workforce.hr.payroll.service;

import com.tbcpl.workforce.hr.payroll.dto.request.HrSalaryStructureRequest;
import com.tbcpl.workforce.hr.payroll.dto.response.HrSalaryStructureResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HrSalaryStructureService {

    /**
     * Create a new salary structure for an employee.
     * Automatically closes any existing open structure.
     */
    HrSalaryStructureResponse createSalaryStructure(HrSalaryStructureRequest request,
                                                    String createdBy);

    /**
     * Get the current active salary structure for an employee.
     */
    HrSalaryStructureResponse getCurrentStructure(String empId);

    /**
     * Get full salary revision history for an employee.
     */
    List<HrSalaryStructureResponse> getSalaryHistory(String empId);

    /**
     * Get salary structure by ID.
     */
    HrSalaryStructureResponse getSalaryStructureById(Long id);

    /**
     * Get all salary structures (HR view, paginated).
     */
    Page<HrSalaryStructureResponse> getAllSalaryStructures(int page, int size);

    HrSalaryStructureResponse getSalaryStructureByEmpId(String empId);

    /**
     * Soft delete a salary structure.
     */
    void deleteSalaryStructure(Long id);
}