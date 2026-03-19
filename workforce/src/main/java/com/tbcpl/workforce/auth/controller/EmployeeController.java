package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.request.EmployeeRequest;
import com.tbcpl.workforce.auth.dto.response.EmployeeResponse;
import com.tbcpl.workforce.auth.service.EmployeeService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.tbcpl.workforce.auth.dto.request.EmployeeUpdateRequest;

import java.io.IOException;
import java.util.List;

/**
 * Controller for Employee management
 * HR and ADMIN can manage employees
 */
@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE)
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * POST /api/v1/auth/employees
     * Create new employee — ADMIN / SUPER_ADMIN only
     */
    @PostMapping(ApiEndpoints.EMPLOYEES)
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create employee request by: {}", createdBy);

        // ✅ Only 2 args — EmailVerificationService injected inside EmployeeService
        EmployeeResponse response = employeeService.createEmployee(request, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }

    // EmployeeController.java — update getAllEmployees:
    @GetMapping(ApiEndpoints.EMPLOYEES)
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) String name    // ← ADD THIS
    ) {
        log.info("Get all employees - page:{} size:{} name:{}", page, size, name);
        Page<EmployeeResponse> employees = employeeService.getAllEmployees(
                page, size, departmentId, roleId, name);   // ← PASS IT
        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }


    /**
     * GET /api/v1/auth/employees/{id}
     * Get employee by DB ID
     */
    @GetMapping(ApiEndpoints.EMPLOYEE_BY_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(
            @PathVariable Long id
    ) {
        log.info("Get employee by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("Employee retrieved successfully",
                        employeeService.getEmployeeById(id))
        );
    }

    /**
     * GET /api/v1/auth/employees/emp/{empId}
     * Get employee by empId (e.g., 2026/001)
     */
    @GetMapping(ApiEndpoints.EMPLOYEE_BY_EMP_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmpId(
            @PathVariable String empId
    ) {
        log.info("Get employee by empId: {}", empId);
        return ResponseEntity.ok(
                ApiResponse.success("Employee retrieved successfully",
                        employeeService.getEmployeeByEmpId(empId))
        );
    }

    /**
     * DELETE /api/v1/auth/employees/{id}
     * Soft delete — ADMIN / SUPER_ADMIN only
     */
    @DeleteMapping(ApiEndpoints.EMPLOYEE_BY_ID)
    public ResponseEntity<ApiResponse<String>> deleteEmployee(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String deletedBy = authentication.getName();
        log.info("Soft delete employee ID: {} by: {}", id, deletedBy);
        employeeService.deleteEmployee(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("Employee deactivated successfully", null));
    }

    /**
     * GET /api/v1/auth/employees/reporting-managers
     * Returns employees eligible as reporting managers
     * (SUPER_ADMIN, ADMIN, MANAGER, HR_MANAGER roles)
     */
    @GetMapping(ApiEndpoints.EMPLOYEE_REPORTING_MGRS)
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getReportingManagers() {
        log.info("Get reporting manager candidates");
        return ResponseEntity.ok(
                ApiResponse.success("Reporting managers retrieved",
                        employeeService.getReportingManagerCandidates())
        );
    }

    /**
     * POST /api/v1/auth/employees/{id}/profile-photo
     * Upload or update profile photo via Cloudinary
     */
    @PostMapping(
            value = ApiEndpoints.EMPLOYEE_PROFILE_PHOTO,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<EmployeeResponse>> uploadProfilePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        log.info("Upload profile photo for employee ID: {}", id);
        EmployeeResponse response = employeeService.uploadProfilePhoto(id, file);
        return ResponseEntity.ok(
                ApiResponse.success("Profile photo uploaded successfully", response));
    }

    @PutMapping(ApiEndpoints.EMPLOYEE_BY_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update employee ID: {} by: {}", id, updatedBy);
        EmployeeResponse response = employeeService.updateEmployee(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", response));
    }

}
