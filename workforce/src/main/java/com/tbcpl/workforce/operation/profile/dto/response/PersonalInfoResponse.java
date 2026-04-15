package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoResponse {
    private String firstName;
    private String middleName;
    private String lastName;

    // ── Was: Gender gender ───────────────────────────────────────────────────
    private String gender;
    private String genderOther;
    // ────────────────────────────────────────────────────────────────────────

    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String nationality;
    private String profilePhoto;
}