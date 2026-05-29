package com.tbcpl.workforce.hr.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.attendance.entity.enums.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendanceResponse {

    private Long             id;
    private String           empId;
    private LocalDate        attendanceDate;
    private AttendanceStatus status;
    private LocalTime        punchInTime;
    private LocalTime        punchOutTime;
    private Double           workingHours;
    private Boolean          isRegularized;
    private String           regularizationReason;
    private String           remarks;
    private Boolean          isActive;
    private LocalDateTime    createdAt;
    private LocalDateTime    updatedAt;
    private String           createdBy;
}