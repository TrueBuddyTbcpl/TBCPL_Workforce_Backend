package com.tbcpl.workforce.config;

import com.tbcpl.workforce.auth.entity.Department;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.Role;
import com.tbcpl.workforce.auth.repository.DepartmentRepository;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.auth.repository.RoleRepository;
import com.tbcpl.workforce.common.enums.DepartmentType;
import com.tbcpl.workforce.common.enums.RoleType;
import com.tbcpl.workforce.common.util.EmpIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private static final String SYSTEM = "SYSTEM";
    private static final String DEFAULT_ADMIN_EMAIL = "control@tbcpl.co.in";
    private static final String DEFAULT_ADMIN_PASSWORD = "TestControl#2026";

    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmpIdGenerator empIdGenerator;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            log.info("==============================================");
            log.info("Starting data seeding...");
            log.info("==============================================");

            seedDepartments();
            seedRoles();
            seedAdminUser();

            log.info("==============================================");
            log.info("Data seeding completed successfully!");
            log.info("==============================================");
        };
    }

    private void seedDepartments() {
        log.info("Checking and seeding department master data...");

        Arrays.stream(DepartmentType.values()).forEach(departmentType ->
                departmentRepository.findByDepartmentNameIgnoreCase(departmentType.name())
                        .ifPresentOrElse(
                                existing -> {
                                    if (Boolean.FALSE.equals(existing.getIsActive())) {
                                        existing.setIsActive(true);
                                        existing.setCreatedBy(SYSTEM);
                                        departmentRepository.save(existing);
                                        log.info("Re-activated department: {}", departmentType.name());
                                    } else {
                                        log.debug("Department already present: {}", departmentType.name());
                                    }
                                },
                                () -> {
                                    Department department = Department.builder()
                                            .departmentName(departmentType.name())
                                            .createdBy(SYSTEM)
                                            .isActive(true)
                                            .build();
                                    departmentRepository.save(department);
                                    log.info("Seeded department: {}", departmentType.name());
                                }
                        )
        );
    }

    private void seedRoles() {
        log.info("Checking and seeding role master data...");

        Arrays.stream(RoleType.values()).forEach(roleType ->
                roleRepository.findByRoleNameIgnoreCase(roleType.getDbValue())
                        .ifPresentOrElse(
                                existing -> {
                                    if (Boolean.FALSE.equals(existing.getIsActive())) {
                                        existing.setIsActive(true);
                                        existing.setCreatedBy(SYSTEM);
                                        roleRepository.save(existing);
                                        log.info("Re-activated role: {}", roleType.getDbValue());
                                    } else {
                                        log.debug("Role already present: {}", roleType.getDbValue());
                                    }
                                },
                                () -> {
                                    Role role = Role.builder()
                                            .roleName(roleType.getDbValue())
                                            .createdBy(SYSTEM)
                                            .isActive(true)
                                            .build();
                                    roleRepository.save(role);
                                    log.info("Seeded role: {}", roleType.getDbValue());
                                }
                        )
        );
    }

    private void seedAdminUser() {
        Optional<Employee> existingAdmin = employeeRepository.findByEmailIgnoreCase(DEFAULT_ADMIN_EMAIL);

        if (existingAdmin.isPresent()) {
            log.info("Admin user already exists with email: {}", DEFAULT_ADMIN_EMAIL);
            return;
        }

        Department adminDepartment = departmentRepository.findByDepartmentNameIgnoreCase(DepartmentType.ADMIN.name())
                .orElseThrow(() -> new IllegalStateException("ADMIN department not found"));

        Role superAdminRole = roleRepository.findByRoleNameIgnoreCase(RoleType.SUPER_ADMIN.getDbValue())
                .orElseThrow(() -> new IllegalStateException("SUPER_ADMIN role not found"));

        String yearPrefix = LocalDate.now().getYear() + "/%";
        Integer lastEmpNumber = employeeRepository.findMaxEmployeeNumberByYear(yearPrefix);
        String empId = empIdGenerator.generateEmpId(lastEmpNumber);

        Employee admin = Employee.builder()
                .empId(empId)
                .email(DEFAULT_ADMIN_EMAIL)
                .password(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD))
                .firstName("System")
                .lastName("Administrator")
                .middleName(null)
                .department(adminDepartment)
                .role(superAdminRole)
                .lastPasswordChangeDate(LocalDate.now())
                .isActive(true)
                .emailVerified(true)
                .createdBy(SYSTEM)
                .build();

        employeeRepository.save(admin);

        log.info("Default admin user seeded successfully with email: {}", DEFAULT_ADMIN_EMAIL);
    }
}