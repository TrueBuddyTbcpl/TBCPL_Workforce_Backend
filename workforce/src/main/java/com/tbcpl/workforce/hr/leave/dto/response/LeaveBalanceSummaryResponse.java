package com.tbcpl.workforce.hr.leave.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveBalanceSummaryResponse {

    private String                    empId;
    private Integer                   year;
    private List<LeaveBalanceResponse> balances;
    private long                      pendingApplicationsCount;
}