package com.tbcpl.workforce.auth.security;

import com.tbcpl.workforce.auth.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Custom UserDetails implementation for Spring Security
 * Maps Employee entity to Spring Security's UserDetails
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Employee employee;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = employee.getRole().getRoleName().toUpperCase();

        // ── FIX: For admin roles, always grant DEPARTMENT_ADMIN authority ────────
        // regardless of which department they are assigned to in the DB.
        // Previously this relied on departmentName which could be wrong for ADMIN role.
        String departmentAuthority = switch (roleName) {
            case "SUPER_ADMIN", "ADMIN" -> "DEPARTMENT_ADMIN";
            case "HR_MANAGER"           -> "DEPARTMENT_HR";
            case "FIELD_ASSOCIATE",
                 "ASSOCIATE"            -> "DEPARTMENT_OPERATION";
            case "ACCOUNTS"             -> "DEPARTMENT_ACCOUNTS";
            // Fallback: derive from department name as before
            default -> "DEPARTMENT_" + employee.getDepartment().getDepartmentName().toUpperCase();
        };

        String roleAuthority = "ROLE_" + roleName;

        return List.of(
                new SimpleGrantedAuthority(departmentAuthority),
                new SimpleGrantedAuthority(roleAuthority)
        );
    }


    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return employee.getIsActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !employee.isPasswordExpired();
    }

    @Override
    public boolean isEnabled() {
        return employee.getIsActive();
    }

    /**
     * Get the underlying Employee entity
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * Get employee ID
     */
    public String getEmpId() {
        return employee.getEmpId();
    }

    /**
     * Get department name
     */
    public String getDepartmentName() {
        return employee.getDepartment().getDepartmentName();
    }

    /**
     * Get role name
     */
    public String getRoleName() {
        return employee.getRole().getRoleName();
    }
}
