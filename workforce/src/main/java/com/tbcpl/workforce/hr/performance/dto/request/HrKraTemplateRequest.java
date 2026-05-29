package com.tbcpl.workforce.hr.performance.dto.request;

import com.tbcpl.workforce.hr.performance.entity.enums.KraStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrKraTemplateRequest {

    @NotBlank(message = "KRA name is required")
    @Size(min = 2, max = 150)
    private String kraName;

    private String kraDescription;

    @Size(max = 100)
    private String designation;

    @Size(max = 50)
    private String department;

    @NotNull(message = "Weightage is required")
    @DecimalMin(value = "1.0",   message = "Weightage must be at least 1")
    @DecimalMax(value = "100.0", message = "Weightage cannot exceed 100")
    private Double weightage;

    @Size(max = 100)
    private String targetValue;

    @Size(max = 50)
    private String measurementUnit;

    private KraStatus status;
}