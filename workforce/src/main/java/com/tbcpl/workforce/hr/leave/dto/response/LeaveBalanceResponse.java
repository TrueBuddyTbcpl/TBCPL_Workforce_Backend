package com.tbcpl.workforce.hr.leave.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveCategory;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveBalanceResponse {

    private Long          id;
    private String        empId;
    private Long          leaveTypeId;
    private String        leaveTypeName;
    private LeaveCategory category;
    private Integer       balanceYear;
    private Double        totalAllocated;
    private Double        totalUsed;
    private Double        totalPending;
    private Double        carriedForward;
    private Double        availableBalance;
    private Boolean       isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}