package com.tbcpl.workforce.hr.payroll.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.payroll.dto.request.HrSalaryStructureRequest;
import com.tbcpl.workforce.hr.payroll.dto.response.HrSalaryStructureResponse;
import com.tbcpl.workforce.hr.payroll.service.HrSalaryStructureService;
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
public class HrSalaryStructureController {

    private final HrSalaryStructureService salaryStructureService;

    /** POST /api/v1/hr/salary-structures */
    @PostMapping(ApiEndpoints.HR_SALARY_STRUCTURES)
    public ResponseEntity<ApiResponse<HrSalaryStructureResponse>> createSalaryStructure(
            @Valid @RequestBody HrSalaryStructureRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create salary structure for empId: {} by: {}", request.getEmpId(), createdBy);
        HrSalaryStructureResponse response =
                salaryStructureService.createSalaryStructure(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Salary structure created successfully", response));
    }

    /** GET /api/v1/hr/salary-structures?page=0&size=20 */
    @GetMapping(ApiEndpoints.HR_SALARY_STRUCTURES)
    public ResponseEntity<ApiResponse<Page<HrSalaryStructureResponse>>> getAllSalaryStructures(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Get all salary structures page:{} size:{}", page, size);
        return ResponseEntity.ok(
                ApiResponse.success("Salary structures retrieved",
                        salaryStructureService.getAllSalaryStructures(page, size)));
    }

    /** GET /api/v1/hr/salary-structures/{id} */
    @GetMapping(ApiEndpoints.HR_SALARY_STRUCTURE_BY_ID)
    public ResponseEntity<ApiResponse<HrSalaryStructureResponse>> getSalaryStructureById(
            @PathVariable Long id
    ) {
        log.info("Get salary structure by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("Salary structure retrieved",
                        salaryStructureService.getSalaryStructureById(id)));
    }

    /** GET /api/v1/hr/salary-structures/emp/{empId}/current */
    @GetMapping(ApiEndpoints.HR_SALARY_STRUCTURE_CURRENT)
    public ResponseEntity<ApiResponse<HrSalaryStructureResponse>> getCurrentStructure(
            @PathVariable String empId
    ) {
        log.info("Get current salary structure for empId: {}", empId);
        return ResponseEntity.ok(
                ApiResponse.success("Current salary structure retrieved",
                        salaryStructureService.getCurrentStructure(empId)));
    }

    /** GET /api/v1/hr/salary-structures/emp/{empId}/history */
    @GetMapping(ApiEndpoints.HR_SALARY_STRUCTURE_HISTORY)
    public ResponseEntity<ApiResponse<List<HrSalaryStructureResponse>>> getSalaryHistory(
            @PathVariable String empId
    ) {
        log.info("Get salary history for empId: {}", empId);
        return ResponseEntity.ok(
                ApiResponse.success("Salary history retrieved",
                        salaryStructureService.getSalaryHistory(empId)));
    }

    /** GET /api/v1/hr/salary-structures/emp/{empId} */
    @GetMapping(ApiEndpoints.HR_SALARY_STRUCTURE_BY_EMP)
    public ResponseEntity<ApiResponse<HrSalaryStructureResponse>> getSalaryStructureByEmpId(
            @PathVariable String empId
    ) {
        log.info("Fetch salary structure for empId:{}", empId);
        return ResponseEntity.ok(ApiResponse.success(
                "Salary structure retrieved",
                salaryStructureService.getSalaryStructureByEmpId(empId)));
    }

    /** DELETE /api/v1/hr/salary-structures/{id} */
    @DeleteMapping(ApiEndpoints.HR_SALARY_STRUCTURE_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteSalaryStructure(
            @PathVariable Long id
    ) {
        log.info("Soft delete salary structure ID: {}", id);
        salaryStructureService.deleteSalaryStructure(id);
        return ResponseEntity.ok(
                ApiResponse.success("Salary structure deleted successfully"));
    }
}