package com.tbcpl.workforce.hr.recruitment.dto.request;

import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrJobRequisitionRequest {

    @NotBlank(message = "Job title is required")
    @Size(min = 2, max = 150, message = "Job title must be between 2 and 150 characters")
    private String jobTitle;

    @NotBlank(message = "Department is required")
    private String department;

    @Size(max = 100)
    private String designation;

    @NotNull(message = "Number of positions is required")
    @Min(value = 1, message = "At least 1 position is required")
    private Integer numberOfPositions;

    private String jobDescription;

    @Min(value = 0, message = "Experience years cannot be negative")
    private Integer requiredExperienceYears;

    @Size(max = 500)
    private String requiredSkills;

    @DecimalMin(value = "0.0", message = "Min budget cannot be negative")
    private Double minSalaryBudget;

    @DecimalMin(value = "0.0", message = "Max budget cannot be negative")
    private Double maxSalaryBudget;

    private LocalDate targetJoiningDate;

    private RecruitmentStatus status;

    @Size(max = 255)
    private String closureRemarks;
}