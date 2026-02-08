package com.tbcpl.workforce.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for resetting employee password
 * Only HR or ADMIN can use this
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
