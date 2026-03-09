package com.tbcpl.workforce.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PasswordChangeRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 50, message = "Password must be 8–50 characters")
    private String newPassword;

    @JsonAlias({"confirmPassword", "confirmNewPassword", "retypePassword"})  // ← FIX
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
