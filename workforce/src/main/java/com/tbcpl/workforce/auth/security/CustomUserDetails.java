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
        // Grant authorities based on department
        String department = "DEPARTMENT_" + employee.getDepartment().getDepartmentName().toUpperCase();
        String role = "ROLE_" + employee.getRole().getRoleName().toUpperCase();

        return List.of(
                new SimpleGrantedAuthority(department),
                new SimpleGrantedAuthority(role)
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
