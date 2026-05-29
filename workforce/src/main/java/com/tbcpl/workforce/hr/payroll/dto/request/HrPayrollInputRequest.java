package com.tbcpl.workforce.hr.payroll.dto.request;

import com.tbcpl.workforce.hr.payroll.entity.enums.PayrollInputType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrPayrollInputRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotNull(message = "Payroll month is required")
    @Min(value = 1,  message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer payrollMonth;

    @NotNull(message = "Payroll year is required")
    @Min(value = 2000, message = "Year must be valid")
    @Max(value = 2100, message = "Year must be valid")
    private Integer payrollYear;

    @NotNull(message = "Input type is required")
    private PayrollInputType inputType;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;

    @Size(max = 255)
    private String description;

    // Only for LEAVE_WITHOUT_PAY type
    @DecimalMin(value = "0.5", message = "LWP days must be at least 0.5")
    @DecimalMax(value = "31.0", message = "LWP days cannot exceed 31")
    private Double lwpDays;
}