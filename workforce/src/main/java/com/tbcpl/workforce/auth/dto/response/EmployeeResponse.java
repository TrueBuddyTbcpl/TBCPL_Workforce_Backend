package com.tbcpl.workforce.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for employee response
 * Password is excluded for security
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;
    private String empId;
    private String email;

    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;

    private Long departmentId;
    private String departmentName;

    private Long roleId;
    private String roleName;

    private LocalDate lastPasswordChangeDate;
    private LocalDateTime lastLoginDate;
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;

    // Password expiry info
    private Boolean passwordExpired;
    private Long daysUntilPasswordExpiry;
    private Boolean showPasswordExpiryWarning;
}
