package com.tbcpl.workforce.config;

import com.tbcpl.workforce.auth.entity.Department;
import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.Role;
import com.tbcpl.workforce.auth.repository.DepartmentRepository;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.auth.repository.RoleRepository;
import com.tbcpl.workforce.common.util.EmpIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Data Seeder - Creates initial departments, roles, and admin user
 * Runs once on application startup if data doesn't exist
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

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

            // Seed Departments
            seedDepartments();

            // Seed Roles
            seedRoles();

            // Seed Admin User
            seedAdminUser();

            log.info("==============================================");
            log.info("Data seeding completed successfully!");
            log.info("==============================================");
        };
    }

    /**
     * Seed initial departments
     */
    private void seedDepartments() {
        if (departmentRepository.count() == 0) {
            log.info("Seeding departments...");

            Department admin = Department.builder()
                    .departmentName("ADMIN")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            Department hr = Department.builder()
                    .departmentName("HR")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            Department operation = Department.builder()
                    .departmentName("OPERATION")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            Department accounts = Department.builder()
                    .departmentName("ACCOUNTS")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            departmentRepository.save(admin);
            departmentRepository.save(hr);
            departmentRepository.save(operation);
            departmentRepository.save(accounts);

            log.info("✅ Departments seeded: ADMIN, HR, OPERATION, ACCOUNTS");
        } else {
            log.info("⏭️  Departments already exist. Skipping seeding.");
        }
    }

    /**
     * Seed initial roles
     */
    private void seedRoles() {
        if (roleRepository.count() == 0) {
            log.info("Seeding roles...");

            Role superAdmin = Role.builder()
                    .roleName("SUPER_ADMIN")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            Role hrManager = Role.builder()
                    .roleName("HR_MANAGER")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            Role manager = Role.builder()
                    .roleName("MANAGER")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            Role teamLead = Role.builder()
                    .roleName("TEAM_LEAD")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            Role staff = Role.builder()
                    .roleName("STAFF")
                    .createdBy("SYSTEM")
                    .isActive(true)
                    .build();

            roleRepository.save(superAdmin);
            roleRepository.save(hrManager);
            roleRepository.save(manager);
            roleRepository.save(teamLead);
            roleRepository.save(staff);

            log.info("✅ Roles seeded: SUPER_ADMIN, HR_MANAGER, MANAGER, TEAM_LEAD, STAFF");
        } else {
            log.info("⏭️  Roles already exist. Skipping seeding.");
        }
    }

    /**
     * Seed initial admin user
     */
    private void seedAdminUser() {
        // Check if admin already exists
        Optional<Employee> existingAdmin = employeeRepository.findByEmailIgnoreCase("admin.2026@gnsp.co.in");

        if (existingAdmin.isPresent()) {
            log.info("⏭️  Admin user already exists");
            log.info("==========================================");
            log.info("   EXISTING ADMIN CREDENTIALS:");
            log.info("==========================================");
            log.info("   Employee ID: {}", existingAdmin.get().getEmpId());
            log.info("   Email: admin.2026@gnsp.co.in");
            log.info("   Password: Admin@123 (if not changed)");
            log.info("==========================================");
            return;
        }

        log.info("Seeding admin user...");

        // Get ADMIN department and SUPER_ADMIN role
        Department adminDept = departmentRepository.findByDepartmentNameIgnoreCase("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN department not found"));

        Role superAdminRole = roleRepository.findByRoleNameIgnoreCase("SUPER_ADMIN")
                .orElseThrow(() -> new RuntimeException("SUPER_ADMIN role not found"));

        // Generate employee ID
        String yearPrefix = LocalDate.now().getYear() + "/%";
        Integer lastEmpNumber = employeeRepository.findMaxEmployeeNumberByYear(yearPrefix);
        String empId = empIdGenerator.generateEmpId(lastEmpNumber);

        // Create admin employee
        Employee admin = Employee.builder()
                .empId(empId)
                .email("admin.2026@gnsp.co.in")
                .password(passwordEncoder.encode("Admin@123")) // Default password
                .firstName("System")
                .lastName("Administrator")
                .middleName(null)
                .department(adminDept)
                .role(superAdminRole)
                .lastPasswordChangeDate(LocalDate.now())
                .isActive(true)
                .createdBy("SYSTEM")
                .build();

        employeeRepository.save(admin);

        log.info("==========================================");
        log.info("   ✅ ADMIN USER CREATED SUCCESSFULLY!");
        log.info("==========================================");
        log.info("   Employee ID: {}", empId);
        log.info("   Email: admin.2026@gnsp.co.in");
        log.info("   Password: Admin@123");
        log.info("==========================================");
        log.info("   ⚠️  IMPORTANT: Change password after first login!");
        log.info("==========================================");
    }
}
