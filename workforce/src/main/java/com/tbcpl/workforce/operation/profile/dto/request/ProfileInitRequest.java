package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileInitRequest {

    @NotBlank(message = "First name is mandatory")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    private String middleName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    // ── Was: @NotNull Gender gender ──────────────────────────────────────────
    @NotBlank(message = "Gender is mandatory")
    private String gender;

    private String genderOther;
    // ────────────────────────────────────────────────────────────────────────

    private String dateOfBirth;

    @Size(max = 10, message = "Blood group must not exceed 10 characters")
    private String bloodGroup;

    @Size(max = 100, message = "Nationality must not exceed 100 characters")
    private String nationality;

    private String profilePhoto;
}