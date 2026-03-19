package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.dto.request.EmployeeRequest;
import com.tbcpl.workforce.auth.dto.response.EmployeeResponse;
import com.tbcpl.workforce.auth.entity.Department;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.Role;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.constants.ValidationMessages;
import com.tbcpl.workforce.common.enums.RoleType;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;

import com.tbcpl.workforce.common.util.S3Service;
import com.tbcpl.workforce.common.util.EmailValidator;
import com.tbcpl.workforce.common.util.EmpIdGenerator;
import com.tbcpl.workforce.common.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.tbcpl.workforce.auth.dto.request.EmployeeUpdateRequest;


import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service for Employee management
 * HR and ADMIN can manage employees
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository       employeeRepository;
    private final DepartmentService        departmentService;
    private final RoleService              roleService;
    private final PasswordEncoder          passwordEncoder;
    private final PasswordValidator        passwordValidator;
    private final EmailValidator           emailValidator;
    private final EmpIdGenerator           empIdGenerator;
    private final S3Service s3Service;
    private final EmailVerificationService emailVerificationService;

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Create new employee.
     * Sends email verification automatically after creation.
     * Called by ADMIN or SUPER_ADMIN only.
     */
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request, String createdBy) {
        log.info("Creating employee: {} {}", request.getFirstName(), request.getLastName());

        int year = LocalDate.now().getYear();

        // CHANGED: Admin provides prefix, backend composes full email
        String emailPrefix = request.getEmailPrefix().toLowerCase().trim();
        String generatedEmail = emailPrefix + "@" + request.getEmailDomain();

        // Validate domain
        emailValidator.validateEmail(generatedEmail);

        // Check duplicate
        if (employeeRepository.existsByEmailIgnoreCase(generatedEmail)) {
            throw new DuplicateResourceException(
                    "Email already exists: " + generatedEmail +
                            ". Please use a different email prefix.");
        }

        passwordValidator.validatePassword(request.getPassword());

        Department department = departmentService.getDepartmentEntityById(request.getDepartmentId());
        Role role = roleService.getRoleEntityById(request.getRoleId());

        Employee reportingManager = null;
        if (request.getReportingManagerEmpId() != null
                && !request.getReportingManagerEmpId().isBlank()) {
            reportingManager = employeeRepository
                    .findByEmpId(request.getReportingManagerEmpId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Reporting manager not found: " + request.getReportingManagerEmpId()));
        }

        String yearPrefix = year + "/%";
        Integer lastEmpNumber = employeeRepository.findMaxEmployeeNumberByYear(yearPrefix);
        String empId = empIdGenerator.generateEmpId(lastEmpNumber);

        Employee employee = Employee.builder()
                .empId(empId)
                .email(generatedEmail)
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .middleName(request.getMiddleName() != null
                        ? request.getMiddleName().trim() : null)
                .department(department)
                .role(role)
                .reportingManager(reportingManager)
                .emailVerified(false)
                .lastPasswordChangeDate(LocalDate.now())
                .isActive(true)
                .createdBy(createdBy)
                .build();

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created: empId={}, email={}", saved.getEmpId(), saved.getEmail());

        emailVerificationService.sendVerificationEmail(saved);

        return mapToResponse(saved);
    }


    // ─────────────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAllEmployees(int page, int size,
                                                  String departmentId, String roleId,
                                                  String name) {   // ← ADD name param
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());

        // If name search is provided — use name filter + optional dept/role
        if (name != null && !name.isBlank()) {
            String searchTerm = "%" + name.trim().toLowerCase() + "%";
            if (departmentId != null && roleId != null) {
                return employeeRepository
                        .findByNameAndDepartmentAndRole(searchTerm,
                                Long.parseLong(departmentId),
                                Long.parseLong(roleId), pageable)
                        .map(this::mapToResponse);
            } else if (departmentId != null) {
                return employeeRepository
                        .findByNameAndDepartment(searchTerm,
                                Long.parseLong(departmentId), pageable)
                        .map(this::mapToResponse);
            } else if (roleId != null) {
                return employeeRepository
                        .findByNameAndRole(searchTerm,
                                Long.parseLong(roleId), pageable)
                        .map(this::mapToResponse);
            }
            return employeeRepository
                    .findByName(searchTerm, pageable)
                    .map(this::mapToResponse);
        }

        // No name search — existing logic unchanged
        if (departmentId != null && roleId != null) {
            return employeeRepository
                    .findByDepartmentIdAndRoleId(Long.parseLong(departmentId),
                            Long.parseLong(roleId), pageable)
                    .map(this::mapToResponse);
        } else if (departmentId != null) {
            return employeeRepository
                    .findByDepartmentId(Long.parseLong(departmentId), pageable)
                    .map(this::mapToResponse);
        } else if (roleId != null) {
            return employeeRepository
                    .findByRoleId(Long.parseLong(roleId), pageable)
                    .map(this::mapToResponse);
        }
        return employeeRepository.findByIsActiveTrue(pageable).map(this::mapToResponse);
    }


    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        log.info("Fetching employee by ID: {}", id);
        return mapToResponse(findById(id));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByEmpId(String empId) {
        log.info("Fetching employee by empId: {}", empId);
        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with empId: " + empId));
        return mapToResponse(employee);
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeEntityByEmail(String email) {
        return employeeRepository.findByEmailWithDepartmentAndRole(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeEntityByEmpId(String empId) {
        return employeeRepository.findByEmpIdWithDepartmentAndRole(empId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with empId: " + empId));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getReportingManagerCandidates() {
        log.info("Fetching reporting manager candidates");
        List<String> managerRoleNames = RoleType.getManagerRoles()
                .stream()
                .map(RoleType::getDbValue)
                .toList();
        return employeeRepository.findActiveEmployeesByRoleNames(managerRoleNames)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void updateLastLoginDate(Long employeeId) {
        Employee employee = findById(employeeId);
        employee.setLastLoginDate(java.time.LocalDateTime.now());
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployeePassword(Long employeeId, String encodedPassword) {
        Employee employee = findById(employeeId);
        employee.setPassword(encodedPassword);
        employee.setLastPasswordChangeDate(LocalDate.now());
        employeeRepository.save(employee);
        log.info("Password updated for employee ID: {}", employeeId);
    }

    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeUpdateRequest request, String updatedBy) {
        log.info("Updating employee ID: {} by: {}", id, updatedBy);

        Employee employee = findById(id);

        // ── Name fields (only update if provided) ────────────────────────────
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            employee.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            employee.setLastName(request.getLastName().trim());
        }
        if (request.getMiddleName() != null) {
            employee.setMiddleName(request.getMiddleName().trim());
        }

        // ── Email (check duplicate before updating) ───────────────────────────
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(employee.getEmail()) &&
                    employeeRepository.existsByEmailIgnoreCase(newEmail)) {
                throw new DuplicateResourceException("Email already exists: " + newEmail);
            }
            employee.setEmail(newEmail);
        }

        // ── Department ────────────────────────────────────────────────────────
        if (request.getDepartmentId() != null) {
            Department department = departmentService.getDepartmentEntityById(request.getDepartmentId());
            employee.setDepartment(department);
        }

        // ── Role ──────────────────────────────────────────────────────────────
        if (request.getRoleId() != null) {
            Role role = roleService.getRoleEntityById(request.getRoleId());
            employee.setRole(role);
        }

        // ── Active status ─────────────────────────────────────────────────────
        if (request.getIsActive() != null) {
            employee.setIsActive(request.getIsActive());
        }

        Employee saved = employeeRepository.save(employee);
        log.info("Employee ID: {} updated successfully by: {}", id, updatedBy);
        return mapToResponse(saved);
    }


    @Transactional
    public void markEmailAsVerified(Long employeeId) {
        Employee employee = findById(employeeId);
        employee.setEmailVerified(true);
        employeeRepository.save(employee);
        log.info("Email marked as verified for employee ID: {}", employeeId);
    }

    @Transactional
    public EmployeeResponse uploadProfilePhoto(Long employeeId, MultipartFile file)
            throws IOException {
        log.info("Uploading profile photo for employee ID: {}", employeeId);
        Employee employee = findById(employeeId);

        if (employee.getProfilePhotoPublicId() != null) {
            try {
                s3Service.deleteFile(employee.getProfilePhotoPublicId());
            } catch (Exception e) {
                log.warn("Failed to delete old profile photo: {}",
                        employee.getProfilePhotoPublicId(), e);
            }
        }

        Map<String, String> result = s3Service.uploadFile(file, "profile-photos");
        employee.setProfilePhotoUrl(result.get("url"));
        employee.setProfilePhotoPublicId(result.get("key"));

        Employee saved = employeeRepository.save(employee);
        log.info("Profile photo uploaded for employee: {}", employee.getEmpId());
        return mapToResponse(saved);
    }




    // ─────────────────────────────────────────────────────────────────────────
    // DELETE (soft)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteEmployee(Long id, String deletedBy) {
        log.info("Soft deleting employee ID: {} by: {}", id, deletedBy);
        Employee employee = findById(id);
        employee.setIsActive(false);
        employeeRepository.save(employee);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(ValidationMessages.EMPLOYEE_NOT_FOUND, id)));
    }

    /**
     * Resend verification email by employee email address.
     * FIX 1: Use findByEmailWithDepartmentAndRole (findByEmail does not exist in repo).
     * FIX 2: Replaced org.apache.coyote.BadRequestException (checked, Tomcat-internal)
     *        with IllegalStateException (unchecked, handled by @ControllerAdvice).
     */
    @Transactional
    public void resendVerificationEmail(String email) {
        Employee employee = employeeRepository.findByEmailWithDepartmentAndRole(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found: " + email));

        if (Boolean.TRUE.equals(employee.getEmailVerified())) {
            throw new IllegalStateException("Email already verified");
        }

        emailVerificationService.resendVerificationEmail(employee);
        log.info("Resend verification request processed for: {}", email);
    }

    public EmployeeResponse mapToResponse(Employee employee) {
        EmployeeResponse.EmployeeResponseBuilder builder = EmployeeResponse.builder()
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
                .profilePhotoUrl(employee.getProfilePhotoUrl())
                .emailVerified(employee.getEmailVerified())
                .passwordExpired(employee.isPasswordExpired())
                .daysUntilPasswordExpiry(employee.getDaysUntilPasswordExpiry())
                .isActive(employee.getIsActive())
                .createdAt(employee.getCreatedAt())
                .createdBy(employee.getCreatedBy());

        if (employee.getReportingManager() != null) {
            builder.reportingManagerEmpId(employee.getReportingManager().getEmpId())
                    .reportingManagerName(employee.getReportingManager().getFullName());
        }

        return builder.build();
    }
}
