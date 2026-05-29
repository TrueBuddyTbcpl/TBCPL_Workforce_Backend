package com.tbcpl.workforce.hr.performance.dto.request;

import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalCycleType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrAppraisalCycleRequest {

    @NotBlank(message = "Cycle name is required")
    @Size(min = 3, max = 150)
    private String cycleName;

    @NotNull(message = "Cycle type is required")
    private AppraisalCycleType cycleType;

    @NotNull(message = "Appraisal year is required")
    @Min(value = 2000)
    @Max(value = 2100)
    private Integer appraisalYear;

    @NotNull(message = "Period start date is required")
    private LocalDate periodStartDate;

    @NotNull(message = "Period end date is required")
    private LocalDate periodEndDate;

    private LocalDate selfReviewStartDate;
    private LocalDate selfReviewEndDate;
    private LocalDate managerReviewEndDate;

    @Size(max = 500)
    private String description;

    @Size(max = 255)
    private String applicableDepartments;
}