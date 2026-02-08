package com.tbcpl.workforce.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn; // milliseconds

    private String empId;
    private String email;
    private String fullName;
    private String firstName;
    private String lastName;

    private Long departmentId;
    private String departmentName;

    private Long roleId;
    private String roleName;

    private Boolean passwordExpired;
    private Long daysUntilPasswordExpiry;
    private String passwordExpiryWarning;
}
