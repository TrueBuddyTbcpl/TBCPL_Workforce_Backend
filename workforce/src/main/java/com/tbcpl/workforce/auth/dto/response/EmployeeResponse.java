package com.tbcpl.workforce.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for Employee data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {

    private Long   id;
    private String empId;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String fullName;

    // Department
    private Long   departmentId;
    private String departmentName;

    // Role
    private Long   roleId;
    private String roleName;

    // Reporting Manager
    private String reportingManagerEmpId;
    private String reportingManagerName;

    // Profile photo (Cloudinary)
    private String profilePhotoUrl;

    // Email verification status
    private Boolean emailVerified;

    // Password state
    private Boolean passwordExpired;
    private Long    daysUntilPasswordExpiry;

    // Status & audit
    private Boolean       isActive;
    private LocalDateTime createdAt;
    private String        createdBy;
}
