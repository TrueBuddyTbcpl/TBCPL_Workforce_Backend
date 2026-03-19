package com.tbcpl.workforce.config;

import com.tbcpl.workforce.auth.entity.Department;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.Role;
import com.tbcpl.workforce.auth.repository.DepartmentRepository;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.auth.repository.RoleRepository;
import com.tbcpl.workforce.common.enums.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Seeds mandatory system roles, default Admin department,
 * and the control super admin account on every startup.
 * Fully idempotent — skips anything that already exists.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository       roleRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository   employeeRepository;
    private final PasswordEncoder      passwordEncoder;

    private static final String SUPER_ADMIN_EMAIL    = "control@tbcpl.co.in";
    private static final String SUPER_ADMIN_PASSWORD = "TestControl#2026";
    private static final String SUPER_ADMIN_EMP_ID   = "2026/000";
    private static final String ADMIN_DEPARTMENT     = "Admin";

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("=== DataInitializer: Starting system seed ===");
        seedRoles();
        seedAdminDepartment();
        seedSuperAdmin();
        log.info("=== DataInitializer: Seed COMPLETED ===");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Seed all RoleType enum values into the roles table
    // ─────────────────────────────────────────────────────────────────────────
    private void seedRoles() {
        log.info("Seeding roles...");
        for (RoleType roleType : RoleType.values()) {
            String dbValue = roleType.getDbValue();
            if (!roleRepository.existsByRoleNameIgnoreCase(dbValue)) {
                roleRepository.save(Role.builder()
                        .roleName(dbValue)
                        .createdBy("SYSTEM")
                        .isActive(true)
                        .build());
                log.info("Seeded role: {}", dbValue);
            } else {
                log.debug("Role already exists, skipping: {}", dbValue);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Seed the "Admin" department (required FK for super admin employee)
    // ─────────────────────────────────────────────────────────────────────────
    private void seedAdminDepartment() {
        if (!departmentRepository.existsByDepartmentNameIgnoreCase(ADMIN_DEPARTMENT)) {
            departmentRepository.save(Department.builder()
                    .departmentName(ADMIN_DEPARTMENT)
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build());
            log.info("Seeded department: {}", ADMIN_DEPARTMENT);
        } else {
            log.debug("Department already exists, skipping: {}", ADMIN_DEPARTMENT);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Seed control@tbcpl.co.in as SUPER_ADMIN
    // emailVerified = false → verification email sent on first login
    // ─────────────────────────────────────────────────────────────────────────
    private void seedSuperAdmin() {
        if (employeeRepository.existsByEmailIgnoreCase(SUPER_ADMIN_EMAIL)) {
            log.debug("Super admin already exists, skipping: {}", SUPER_ADMIN_EMAIL);
            return;
        }

        Role superAdminRole = roleRepository
                .findByRoleNameIgnoreCase(RoleType.SUPER_ADMIN.getDbValue())
                .orElseThrow(() -> new IllegalStateException(
                        "SUPER_ADMIN role not found — seedRoles() must run first"));

        Department adminDept = departmentRepository
                .findByDepartmentNameIgnoreCase(ADMIN_DEPARTMENT)
                .orElseThrow(() -> new IllegalStateException(
                        "Admin department not found — seedAdminDepartment() must run first"));

        employeeRepository.save(Employee.builder()
                .empId(SUPER_ADMIN_EMP_ID)
                .email(SUPER_ADMIN_EMAIL)
                .password(passwordEncoder.encode(SUPER_ADMIN_PASSWORD))
                .firstName("Control")
                .lastName("Admin")
                .department(adminDept)
                .role(superAdminRole)
                .emailVerified(false)       // ← Verification email sent on first login
                .lastPasswordChangeDate(LocalDate.now())
                .isActive(true)
                .createdBy("SYSTEM")
                .build());

        log.info("Super admin seeded: {} (empId: {})", SUPER_ADMIN_EMAIL, SUPER_ADMIN_EMP_ID);
    }
}
