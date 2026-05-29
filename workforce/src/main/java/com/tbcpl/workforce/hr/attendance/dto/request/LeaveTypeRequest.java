package com.tbcpl.workforce.hr.attendance.dto.request;

import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveCategory;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeRequest {

    @NotBlank(message = "Leave type name is required")
    @Size(min = 2, max = 100, message = "Leave type name must be between 2 and 100 characters")
    private String leaveTypeName;

    @NotNull(message = "Leave category is required")
    private LeaveCategory category;

    @Size(max = 255)
    private String description;

    @NotNull(message = "Max days per year is required")
    @Min(value = 1,   message = "Max days must be at least 1")
    @Max(value = 365, message = "Max days cannot exceed 365")
    private Integer maxDaysPerYear;

    private Boolean isCarryForwardAllowed;

    @Min(value = 0,   message = "Carry forward days cannot be negative")
    @Max(value = 365, message = "Carry forward days cannot exceed 365")
    private Integer maxCarryForwardDays;

    private Boolean isHalfDayAllowed;

    private Boolean isPaid;

    @Min(value = 0,  message = "Notice days cannot be negative")
    @Max(value = 30, message = "Notice days cannot exceed 30")
    private Integer minNoticeDays;
}