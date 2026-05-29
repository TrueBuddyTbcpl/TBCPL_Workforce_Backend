package com.tbcpl.workforce.hr.attendance.dto.request;

import com.tbcpl.workforce.hr.attendance.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotNull(message = "Attendance date is required")
    private LocalDate attendanceDate;

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    private LocalTime punchInTime;

    private LocalTime punchOutTime;

    private Double workingHours;

    private Boolean isRegularized;

    @Size(max = 255)
    private String regularizationReason;

    @Size(max = 255)
    private String remarks;
}