package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.dto.request.EmployeeRequest;
import com.tbcpl.workforce.auth.dto.request.EmployeeUpdateRequest;
import com.tbcpl.workforce.auth.dto.response.EmployeeListResponse;
import com.tbcpl.workforce.auth.dto.response.EmployeeResponse;
import com.tbcpl.workforce.auth.entity.Department;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.Role;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.constants.ValidationMessages;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.InvalidCredentialsException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmpIdGenerator;
import com.tbcpl.workforce.common.util.EmailValidator;
import com.tbcpl.workforce.common.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service class for Employee management
 * HR and ADMIN can create/update/delete employees
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final EmpIdGenerator empIdGenerator;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;

    /**
     * Create new employee
     * HR and ADMIN can use this
     */
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request, String createdBy) {
        log.info("Creating employee with email: {}", request.getEmail());

        // Validate email format
        emailValidator.validateEmail(request.getEmail());

        // Validate password format
        passwordValidator.validatePassword(request.getPassword());

        // Check if email already exists
        if (employeeRepository.existsByEmailIgnoreCase(request.getEmail())) {
            log.error("Email already exists: {}", request.getEmail());
            throw new DuplicateResourceException(
                    String.format(ValidationMessages.EMPLOYEE_EMAIL_EXISTS, request.getEmail())
            );
        }

        // Get department and role entities
        Department department = departmentService.getDepartmentEntityById(request.getDepartmentId());
        Role role = roleService.getRoleEntityById(request.getRoleId());

        // Generate employee ID
        String yearPrefix = LocalDate.now().getYear() + "/%";
        Integer lastEmpNumber = employeeRepository.findMaxEmployeeNumberByYear(yearPrefix);
        String empId = empIdGenerator.generateEmpId(lastEmpNumber);

        // Build employee entity
        Employee employee = Employee.builder()
                .empId(empId)
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .middleName(request.getMiddleName() != null ? request.getMiddleName().trim() : null)
                .department(department)
                .role(role)
                .lastPasswordChangeDate(LocalDate.now())
                .isActive(true)
                .createdBy(createdBy)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee created successfully with ID: {} and EmpID: {}", savedEmployee.getId(), savedEmployee.getEmpId());

        return mapToResponse(savedEmployee);
    }

    /**
     * Get all employees with pagination
     */
    @Transactional(readOnly = true)
    public EmployeeListResponse getAllEmployees(Pageable pageable) {
        log.info("Fetching all employees with pagination");
        Page<Employee> employeePage = employeeRepository.findByIsActiveTrue(pageable);
        return mapToListResponse(employeePage);
    }

    /**
     * Get employee by ID
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        log.info("Fetching employee by ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.EMPLOYEE_NOT_FOUND, id)
                    );
                });
        return mapToResponse(employee);
    }

    /**
     * Get employee by empId (e.g., 2026/001)
     */
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmpId(String empId) {
        log.info("Fetching employee by EmpID: {}", empId);
        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> {
                    log.error("Employee not found with EmpID: {}", empId);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.EMPLOYEE_NOT_FOUND, empId)
                    );
                });
        return mapToResponse(employee);
    }

    /**
     * Get employees by department with pagination
     */
    @Transactional(readOnly = true)
    public EmployeeListResponse getEmployeesByDepartment(Long departmentId, Pageable pageable) {
        log.info("Fetching employees by department ID: {}", departmentId);
        Page<Employee> employeePage = employeeRepository.findByDepartmentId(departmentId, pageable);
        return mapToListResponse(employeePage);
    }

    /**
     * Get employees by role with pagination
     */
    @Transactional(readOnly = true)
    public EmployeeListResponse getEmployeesByRole(Long roleId, Pageable pageable) {
        log.info("Fetching employees by role ID: {}", roleId);
        Page<Employee> employeePage = employeeRepository.findByRoleId(roleId, pageable);
        return mapToListResponse(employeePage);
    }

    /**
     * Get employees by department and role with pagination
     */
    @Transactional(readOnly = true)
    public EmployeeListResponse getEmployeesByDepartmentAndRole(Long departmentId, Long roleId, Pageable pageable) {
        log.info("Fetching employees by department ID: {} and role ID: {}", departmentId, roleId);
        Page<Employee> employeePage = employeeRepository.findByDepartmentIdAndRoleId(departmentId, roleId, pageable);
        return mapToListResponse(employeePage);
    }

    /**
     * Search employees by name
     */
    @Transactional(readOnly = true)
    public EmployeeListResponse searchEmployeesByName(String searchTerm, Pageable pageable) {
        log.info("Searching employees by name: {}", searchTerm);
        Page<Employee> employeePage = employeeRepository.searchByName(searchTerm, pageable);
        return mapToListResponse(employeePage);
    }

    /**
     * Update employee
     * HR and ADMIN can use this
     */
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request, String updatedBy) {
        log.info("Updating employee ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.EMPLOYEE_NOT_FOUND, id)
                    );
                });

        // Update email if provided
        if (request.getEmail() != null) {
            emailValidator.validateEmail(request.getEmail());

            // Check if new email already exists (excluding current employee)
            employeeRepository.findByEmailIgnoreCase(request.getEmail())
                    .ifPresent(existingEmp -> {
                        if (!existingEmp.getId().equals(id)) {
                            log.error("Email already exists: {}", request.getEmail());
                            throw new DuplicateResourceException(
                                    String.format(ValidationMessages.EMPLOYEE_EMAIL_EXISTS, request.getEmail())
                            );
                        }
                    });
            employee.setEmail(request.getEmail().toLowerCase().trim());
        }

        // Update names if provided
        if (request.getFirstName() != null) {
            employee.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null) {
            employee.setLastName(request.getLastName().trim());
        }
        if (request.getMiddleName() != null) {
            employee.setMiddleName(request.getMiddleName().trim());
        }

        // Update department if provided
        if (request.getDepartmentId() != null) {
            Department department = departmentService.getDepartmentEntityById(request.getDepartmentId());
            employee.setDepartment(department);
        }

        // Update role if provided
        if (request.getRoleId() != null) {
            Role role = roleService.getRoleEntityById(request.getRoleId());
            employee.setRole(role);
        }

        // Update active status if provided
        if (request.getIsActive() != null) {
            employee.setIsActive(request.getIsActive());
        }

        employee.setCreatedBy(updatedBy); // Using createdBy field for last updated by

        Employee updatedEmployee = employeeRepository.save(employee);
        log.info("Employee updated successfully: {}", updatedEmployee.getId());

        return mapToResponse(updatedEmployee);
    }


    /**
     * Update employee password (internal use only)
     */
    @Transactional
    public void updateEmployeePassword(Long employeeId, String newEncodedPassword) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ValidationMessages.EMPLOYEE_NOT_FOUND, employeeId)
                ));

        employee.setPassword(newEncodedPassword);
        employee.setLastPasswordChangeDate(LocalDate.now());
        employeeRepository.save(employee);
    }

    /**
     * Delete employee (soft delete)
     * HR and ADMIN can use this
     */
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Employee not found with ID: {}", id);
                    return new ResourceNotFoundException(
                            String.format(ValidationMessages.EMPLOYEE_NOT_FOUND, id)
                    );
                });

        // Soft delete
        employee.setIsActive(false);
        employeeRepository.save(employee);
        log.info("Employee soft deleted successfully: {}", id);
    }

    /**
     * Update employee's last login date
     */
    @Transactional
    public void updateLastLoginDate(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ValidationMessages.EMPLOYEE_NOT_FOUND, employeeId)
                ));
        employee.setLastLoginDate(LocalDateTime.now());
        employeeRepository.save(employee);
    }

    /**
     * Get employee entity by email (for internal use)
     */
    @Transactional(readOnly = true)
    public Employee getEmployeeEntityByEmail(String email) {
        return employeeRepository.findByEmailWithDepartmentAndRole(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with email: " + email));
    }


    /**
     * Map Employee entity to EmployeeResponse DTO
     */
    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .empId(employee.getEmpId())
                .email(employee.getEmail())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .middleName(employee.getMiddleName())
                .fullName(employee.getFullName())
                .departmentId(employee.getDepartment().getId())
                .departmentName(employee.getDepartment().getDepartmentName())
                .roleId(employee.getRole().getId())
                .roleName(employee.getRole().getRoleName())
                .lastPasswordChangeDate(employee.getLastPasswordChangeDate())
                .lastLoginDate(employee.getLastLoginDate())
                .isActive(employee.getIsActive())
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .createdBy(employee.getCreatedBy())
                .passwordExpired(employee.isPasswordExpired())
                .daysUntilPasswordExpiry(employee.getDaysUntilPasswordExpiry())
                .showPasswordExpiryWarning(employee.shouldShowPasswordExpiryWarning())
                .build();
    }

    /**
     * Map Page<Employee> to EmployeeListResponse
     */
    private EmployeeListResponse mapToListResponse(Page<Employee> employeePage) {
        return EmployeeListResponse.builder()
                .employees(employeePage.getContent().stream()
                        .map(this::mapToResponse)
                        .toList())
                .totalElements(employeePage.getTotalElements())
                .totalPages(employeePage.getTotalPages())
                .currentPage(employeePage.getNumber())
                .pageSize(employeePage.getSize())
                .hasNext(employeePage.hasNext())
                .hasPrevious(employeePage.hasPrevious())
                .build();
    }
}
