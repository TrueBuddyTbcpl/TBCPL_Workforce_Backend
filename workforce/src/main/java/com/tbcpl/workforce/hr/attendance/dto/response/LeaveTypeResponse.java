package com.tbcpl.workforce.hr.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveCategory;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveTypeResponse {

    private Long          id;
    private String        leaveTypeName;
    private LeaveCategory category;
    private String        description;
    private Integer       maxDaysPerYear;
    private Boolean       isCarryForwardAllowed;
    private Integer       maxCarryForwardDays;
    private Boolean       isHalfDayAllowed;
    private Boolean       isPaid;
    private Integer       minNoticeDays;
    private Boolean       isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        createdBy;
}