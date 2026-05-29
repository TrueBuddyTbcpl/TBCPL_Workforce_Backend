package com.tbcpl.workforce.auth.controller;

import com.tbcpl.workforce.auth.dto.request.EmployeeRequest;
import com.tbcpl.workforce.auth.dto.request.EmployeeUpdateRequest;
import com.tbcpl.workforce.auth.dto.response.EmployeeResponse;
import com.tbcpl.workforce.auth.repository.DepartmentRepository;
import com.tbcpl.workforce.auth.repository.RoleRepository;
import com.tbcpl.workforce.auth.service.EmployeeService;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.enums.DepartmentType;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(ApiEndpoints.AUTH_BASE)
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;

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

    @GetMapping(ApiEndpoints.EMPLOYEES)
    public ResponseEntity<ApiResponse<Page<EmployeeResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String departmentId,
            @RequestParam(required = false) String roleId,
            @RequestParam(required = false) String name
    ) {
        log.info("Get all employees - page:{} size:{} name:{}", page, size, name);
        Page<EmployeeResponse> employees =
                employeeService.getAllEmployees(page, size, departmentId, roleId, name);

        return ResponseEntity.ok(ApiResponse.success("Employees retrieved successfully", employees));
    }

    @GetMapping(ApiEndpoints.EMPLOYEE_BY_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        log.info("Get employee by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("Employee retrieved successfully", employeeService.getEmployeeById(id))
        );
    }

    @GetMapping(ApiEndpoints.EMPLOYEE_BY_EMP_ID)
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByEmpId(@PathVariable String empId) {
        log.info("Get employee by empId: {}", empId);
        return ResponseEntity.ok(
                ApiResponse.success("Employee retrieved successfully", employeeService.getEmployeeByEmpId(empId))
        );
    }

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

    @GetMapping(ApiEndpoints.EMPLOYEE_REPORTING_MGRS)
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getReportingManagers() {
        log.info("Get reporting manager candidates");
        return ResponseEntity.ok(
                ApiResponse.success("Reporting managers retrieved",
                        employeeService.getReportingManagerCandidates())
        );
    }

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
                ApiResponse.success("Profile photo uploaded successfully", response)
        );
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

    @GetMapping(ApiEndpoints.META_DEPARTMENTS)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDepartments() {
        log.debug("Fetching all department options");

        List<Map<String, Object>> departments = Arrays.stream(DepartmentType.values())
                .map(deptEnum -> departmentRepository.findByDepartmentNameIgnoreCase(deptEnum.name())
                        .map(dbDept -> Map.<String, Object>of(
                                "id", dbDept.getId(),
                                "value", deptEnum.name(),
                                "label", deptEnum.getDisplayName(),
                                "departmentName", deptEnum.name()
                        ))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Departments fetched successfully", departments)
        );
    }

    @GetMapping(ApiEndpoints.META_ROLES)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRolesByDepartment(
            @RequestParam String department
    ) {
        log.debug("Fetching roles for department: {}", department);

        DepartmentType deptType = DepartmentType.fromString(department);

        List<Map<String, Object>> roles = deptType.getAllowedRoles().stream()
                .map(roleEnum -> roleRepository.findByRoleNameIgnoreCase(roleEnum.getDbValue())
                        .map(dbRole -> Map.<String, Object>of(
                                "id", dbRole.getId(),
                                "value", roleEnum.getDbValue(),
                                "label", roleEnum.getDisplayName()
                        ))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success("Roles fetched for: " + deptType.getDisplayName(), roles)
        );
    }
}