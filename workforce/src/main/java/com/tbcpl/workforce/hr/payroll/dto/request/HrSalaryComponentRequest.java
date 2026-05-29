package com.tbcpl.workforce.hr.payroll.dto.request;

import com.tbcpl.workforce.hr.payroll.entity.enums.ComponentCalculationType;
import com.tbcpl.workforce.hr.payroll.entity.enums.SalaryComponentType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrSalaryComponentRequest {

    @NotBlank(message = "Component name is required")
    @Size(min = 2, max = 100, message = "Component name must be between 2 and 100 characters")
    private String componentName;

    @NotNull(message = "Component type is required")
    private SalaryComponentType componentType;

    @NotNull(message = "Calculation type is required")
    private ComponentCalculationType calculationType;

    // Required for PERCENTAGE_* types
    @DecimalMin(value = "0.01", message = "Percentage must be greater than 0")
    @DecimalMax(value = "100.0", message = "Percentage cannot exceed 100")
    private Double percentageValue;

    // Required for FLAT_AMOUNT or STATUTORY
    @DecimalMin(value = "0.0", message = "Flat amount cannot be negative")
    private Double flatAmount;

    private Boolean isStatutory;

    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder;
}