package com.tbcpl.workforce.auth.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    // REMOVED: email (full email) — now split into prefix + domain

    @NotBlank(message = "Email prefix is required")
    @Pattern(
            regexp = "^[a-zA-Z0-9]([a-zA-Z0-9._-]*[a-zA-Z0-9])?$",
            message = "Email prefix can only contain letters, numbers, dots, hyphens and underscores"
    )
    @Size(min = 2, max = 50, message = "Email prefix must be between 2 and 50 characters")
    private String emailPrefix;  // e.g. "john.doe"

    @NotBlank(message = "Email domain is required")
    @Pattern(
            regexp = "tbcpl\\.co\\.in|gnsp\\.co\\.in",
            message = "Email domain must be tbcpl.co.in or gnsp.co.in"
    )
    private String emailDomain;  // e.g. "tbcpl.co.in"

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(max = 50, message = "Middle name must not exceed 50 characters")
    private String middleName;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Role ID is required")
    private Long roleId;

    private String reportingManagerEmpId;
}
