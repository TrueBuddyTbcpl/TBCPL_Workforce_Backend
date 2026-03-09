package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.request.DepartmentRequest;
import com.tbcpl.workforce.auth.dto.response.DepartmentResponse;
import com.tbcpl.workforce.auth.service.DepartmentService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for Department management
 * Only ADMIN can create/update/delete
 * HR and ADMIN can view
 */
@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE)
@RequiredArgsConstructor
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Create new department
     * Only ADMIN can access
     */
    @PostMapping(ApiEndpoints.DEPARTMENTS)
    public ResponseEntity<ApiResponse<DepartmentResponse>> createDepartment(
            @Valid @RequestBody DepartmentRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create department request by: {}", createdBy);

        DepartmentResponse response = departmentService.createDepartment(request, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Department created successfully", response));
    }

    /**
     * Get all departments
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.DEPARTMENTS)
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAllDepartments() {
        log.info("Get all departments request");

        List<DepartmentResponse> departments = departmentService.getAllDepartments();

        return ResponseEntity.ok(
                ApiResponse.success("Departments retrieved successfully", departments)
        );
    }

    /**
     * Get department by ID
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.DEPARTMENT_BY_ID)
    public ResponseEntity<ApiResponse<DepartmentResponse>> getDepartmentById(
            @PathVariable Long id
    ) {
        log.info("Get department by ID: {}", id);

        DepartmentResponse department = departmentService.getDepartmentById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Department retrieved successfully", department)
        );
    }

    /**
     * Update department
     * Only ADMIN can access
     */
    @PutMapping(ApiEndpoints.DEPARTMENT_BY_ID)
    public ResponseEntity<ApiResponse<DepartmentResponse>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update department ID: {} by: {}", id, updatedBy);

        DepartmentResponse response = departmentService.updateDepartment(id, request, updatedBy);

        return ResponseEntity.ok(
                ApiResponse.success("Department updated successfully", response)
        );
    }

    /**
     * Delete department (soft delete)
     * Only ADMIN can access
     */
    @DeleteMapping(ApiEndpoints.DEPARTMENT_BY_ID)
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id) {
        log.info("Delete department ID: {}", id);

        departmentService.deleteDepartment(id);

        return ResponseEntity.ok(
                ApiResponse.success("Department deleted successfully", null)
        );
    }
}
