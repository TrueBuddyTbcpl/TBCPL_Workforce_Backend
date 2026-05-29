package com.tbcpl.workforce.hr.grievance.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrGrievanceRemarkRequest {

    @NotBlank(message = "Remark cannot be empty")
    @Size(min = 2, max = 2000, message = "Remark must be between 2 and 2000 characters")
    private String remark;

    // EMPLOYEE / HR / MANAGER / ADMIN
    @Size(max = 30)
    private String remarkedByRole;

    // HR-only internal notes — not visible to employee
    private Boolean isInternal;
}