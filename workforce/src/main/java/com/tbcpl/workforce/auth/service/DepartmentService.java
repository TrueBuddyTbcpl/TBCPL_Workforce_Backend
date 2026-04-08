package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.dto.request.DepartmentRequest;
import com.tbcpl.workforce.auth.dto.response.DepartmentResponse;
import com.tbcpl.workforce.auth.entity.Department;
import com.tbcpl.workforce.auth.repository.DepartmentRepository;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.constants.ValidationMessages;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for Department management
 * Only ADMIN can create/update/delete departments
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeNameResolverService nameResolver;

    /**
     * Create new department
     * Only ADMIN can use this
     */
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request, String createdBy) {
        log.info("Creating department: {}", request.getDepartmentName());

        // Check if department already exists
        if (departmentRepository.existsByDepartmentNameIgnoreCase(request.getDepartmentName())) {
            log.error("Department already exists: {}", request.getDepartmentName());
            throw new DuplicateResourceException(
                    String.format(ValidationMessages.DEPARTMENT_NAME_EXISTS, request.getDepartmentName())
            );
        }

        Department department = Department.builder()
                .departmentName(request.getDepartmentName().trim())
                .createdBy(createdBy)
                .isActive(true)
                .build();

        Department savedDepartment = departmentRepository.save(department);
        log.info("Department created successfully with ID: {}", savedDepartment.getId());

        return mapToResponse(savedDepartment);
    }

    /**
     * Get all departments
     */
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        log.info("Fetching all departments");
        List<Department> departments = departmentRepository.findAllByOrderByDepartmentNameAsc();

        Map<String, String> nameMap = nameResolver.resolve(
                departments.stream()
                        .map(Department::getCreatedBy)
                        .filter(v -> v != null && !v.isBlank())
                        .collect(Collectors.toSet())
        );

        // ✅ Lambda — NOT this::mapToResponse (would be ambiguous)
        return departments.stream()
                .map(d -> mapToResponse(d, nameMap))
                .collect(Collectors.toList());
    }

    /**
     * Get all active departments
     */
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getActiveDepartments() {
        log.info("Fetching all active departments");
        List<Department> departments = departmentRepository.findByIsActiveTrue();

        Map<String, String> nameMap = nameResolver.resolve(
                departments.stream()
                        .map(Department::getCreatedBy)
                        .filter(v -> v != null && !v.isBlank())
                        .collect(Collectors.toSet())
        );

        return departments.stream()
                .map(d -> mapToResponse(d, nameMap))   // ✅ lambda
                .collect(Collectors.toList());
    }

    /**
     * Get department by ID
     */
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        log.info("Fetching department by ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ValidationMessages.DEPARTMENT_NOT_FOUND, id)));
        return mapToResponse(department,
                nameResolver.resolve(
                        department.getCreatedBy() != null
                                ? Set.of(department.getCreatedBy())
                                : Collections.emptySet()
                )
        );
    }

    private DepartmentResponse mapToResponse(Department department) {
        if (department.getCreatedBy() == null || department.getCreatedBy().isBlank()) {
            return mapToResponse(department, Collections.emptyMap());
        }
        return mapToResponse(department,
                nameResolver.resolve(Set.of(department.getCreatedBy()))
        );
    }

    /**
     * Update department
     * Only ADMIN can use this
     */
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request, String updatedBy) {
        log.info("Updating department ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.DEPARTMENT_NOT_FOUND, id)
                    );
                });

        // Check if new name already exists (excluding current department)
        departmentRepository.findByDepartmentNameIgnoreCase(request.getDepartmentName())
                .ifPresent(existingDept -> {
                    if (!existingDept.getId().equals(id)) {
                        log.error("Department name already exists: {}", request.getDepartmentName());
                        throw new DuplicateResourceException(
                                String.format(ValidationMessages.DEPARTMENT_NAME_EXISTS, request.getDepartmentName())
                        );
                    }
                });

        department.setDepartmentName(request.getDepartmentName().trim());
        department.setCreatedBy(updatedBy); // Using createdBy field for last updated by

        Department updatedDepartment = departmentRepository.save(department);
        log.info("Department updated successfully: {}", updatedDepartment.getId());

        return mapToResponse(updatedDepartment);
    }

    /**
     * Delete department (soft delete)
     * Only ADMIN can use this
     */
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Deleting department ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Department not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.DEPARTMENT_NOT_FOUND, id)
                    );
                });

        // Check if department is in use
        long employeeCount = departmentRepository.countEmployeesByDepartmentId(id);
        if (employeeCount > 0) {
            log.error("Cannot delete department. It is assigned to {} employee(s)", employeeCount);
            throw new IllegalArgumentException(
                    String.format(ValidationMessages.DEPARTMENT_IN_USE, employeeCount)
            );
        }

        // Soft delete
        department.setIsActive(false);
        departmentRepository.save(department);
        log.info("Department soft deleted successfully: {}", id);
    }

    /**
     * Get department entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public Department getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ValidationMessages.DEPARTMENT_NOT_FOUND, id)
                ));
    }

    /**
     * Map Department entity to DepartmentResponse DTO
     */
    private DepartmentResponse mapToResponse(Department department, Map<String, String> nameMap) {
        String raw = department.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(
                        raw.contains("@") ? raw.toLowerCase() : raw,
                        raw   // fallback: show original value if employee not found
                );

        return DepartmentResponse.builder()
                .id(department.getId())
                .departmentName(department.getDepartmentName())
                .isActive(department.getIsActive())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}
