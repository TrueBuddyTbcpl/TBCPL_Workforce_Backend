package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.request.EmployeeRequest;
import com.tbcpl.workforce.auth.dto.request.EmployeeUpdateRequest;
import com.tbcpl.workforce.auth.dto.response.EmployeeListResponse;
import com.tbcpl.workforce.auth.dto.response.EmployeeResponse;
import com.tbcpl.workforce.auth.service.EmployeeService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for Employee management
 * HR and ADMIN can create/update/delete/view
 */
@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE)
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Create new employee
     * HR and ADMIN can access
     */
    @PostMapping(ApiEndpoints.EMPLOYEES)
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create employee request by: {}", createdBy);

        EmployeeResponse response = employeeService.createEmployee(request, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", response));
    }

    /**
     * Get all employees with pagination
     * HR and ADMIN can access
     *
     * Query params:
     * - page: page number (default: 0)
     * - size: page size (default: 10)
     * - sort: sort field (default: empId)
     * - direction: sort direction (default: ASC)
     */
    @GetMapping(ApiEndpoints.EMPLOYEES)
    public ResponseEntity<ApiResponse<EmployeeListResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "empId") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        log.info("Get all employees - page: {}, size: {}, sort: {}, direction: {}",
                page, size, sort, direction);

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        EmployeeListResponse response = employeeService.getAllEmployees(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Employees retrieved successfully", response)
        );
    }

    /**
     * Get employee by ID
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.EMPLOYEE_BY_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        log.info("Get employee by ID: {}", id);

        EmployeeResponse employee = employeeService.getEmployeeById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Employee retrieved successfully", employee)
        );
    }

    @GetMapping(ApiEndpoints.EMPLOYEE_BY_DATABASE_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByDatabaseId(@PathVariable Long id) {
        log.info("Get employee by database ID: {}", id);

        EmployeeResponse employee = employeeService.getEmployeeById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Employee retrieved successfully", employee)
        );
    }

    /**
     * Get employee by empId (e.g., 2026/001)
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.EMPLOYEE_BY_EMP_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmpId(@PathVariable String empId) {
        log.info("Get employee by EmpID: {}", empId);

        EmployeeResponse employee = employeeService.getEmployeeByEmpId(empId);

        return ResponseEntity.ok(
                ApiResponse.success("Employee retrieved successfully", employee)
        );
    }

    /**
     * Filter employees by department and/or role with pagination
     * HR and ADMIN can access
     *
     * Query params:
     * - departmentId (optional): filter by department
     * - roleId (optional): filter by role
     * - page: page number (default: 0)
     * - size: page size (default: 10)
     */
    @GetMapping(ApiEndpoints.EMPLOYEES_FILTER)
    public ResponseEntity<ApiResponse<EmployeeListResponse>> filterEmployees(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long roleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Filter employees - department: {}, role: {}", departmentId, roleId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("empId").ascending());
        EmployeeListResponse response;

        if (departmentId != null && roleId != null) {
            response = employeeService.getEmployeesByDepartmentAndRole(departmentId, roleId, pageable);
        } else if (departmentId != null) {
            response = employeeService.getEmployeesByDepartment(departmentId, pageable);
        } else if (roleId != null) {
            response = employeeService.getEmployeesByRole(roleId, pageable);
        } else {
            response = employeeService.getAllEmployees(pageable);
        }

        return ResponseEntity.ok(
                ApiResponse.success("Employees filtered successfully", response)
        );
    }

    /**
     * Search employees by name
     * HR and ADMIN can access
     */
    @GetMapping(ApiEndpoints.EMPLOYEES + "/search")
    public ResponseEntity<ApiResponse<EmployeeListResponse>> searchEmployees(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("Search employees by name: {}", name);

        Pageable pageable = PageRequest.of(page, size);
        EmployeeListResponse response = employeeService.searchEmployeesByName(name, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Search completed successfully", response)
        );
    }

    /**
     * Update employee
     * HR and ADMIN can access
     */
    @PutMapping(ApiEndpoints.EMPLOYEE_BY_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update employee ID: {} by: {}", id, updatedBy);

        EmployeeResponse response = employeeService.updateEmployee(id, request, updatedBy);

        return ResponseEntity.ok(
                ApiResponse.success("Employee updated successfully", response)
        );
    }

    /**
     * Delete employee (soft delete)
     * HR and ADMIN can access
     */
    @DeleteMapping(ApiEndpoints.EMPLOYEE_BY_ID)
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable Long id) {
        log.info("Delete employee ID: {}", id);

        employeeService.deleteEmployee(id);

        return ResponseEntity.ok(
                ApiResponse.success("Employee deleted successfully", null)
        );
    }
}
