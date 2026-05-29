package com.tbcpl.workforce.hr.leave.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveApplicationResponse {

    private Long        id;
    private String      empId;

    // Leave type details
    private Long        leaveTypeId;
    private String      leaveTypeName;
    private String      leaveCategory;

    private LocalDate   fromDate;
    private LocalDate   toDate;
    private Double      numberOfDays;
    private Boolean     isHalfDay;
    private String      reason;
    private LeaveStatus status;

    // Review details
    private String        reviewedBy;
    private LocalDateTime reviewedAt;
    private String        reviewerRemarks;

    private Boolean       isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        createdBy;
}