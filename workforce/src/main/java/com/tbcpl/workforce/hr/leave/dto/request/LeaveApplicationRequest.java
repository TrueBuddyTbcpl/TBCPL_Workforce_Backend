package com.tbcpl.workforce.hr.leave.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApplicationRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    @NotNull(message = "From date is required")
    private LocalDate fromDate;

    @NotNull(message = "To date is required")
    private LocalDate toDate;

    private Boolean isHalfDay;

    @NotBlank(message = "Reason is required")
    @Size(min = 5, max = 500, message = "Reason must be between 5 and 500 characters")
    private String reason;
}