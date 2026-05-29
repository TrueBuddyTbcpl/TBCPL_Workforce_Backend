package com.tbcpl.workforce.hr.payroll.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrSalaryStructureRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotNull(message = "Annual CTC is required")
    @DecimalMin(value = "1.0", message = "Annual CTC must be greater than 0")
    private Double annualCtc;

    @NotNull(message = "Effective from date is required")
    private LocalDate effectiveFrom;

    @Size(max = 255)
    private String revisionRemarks;

    @NotEmpty(message = "At least one salary component is required")
    @Valid
    private List<HrSalaryComponentRequest> components;
}