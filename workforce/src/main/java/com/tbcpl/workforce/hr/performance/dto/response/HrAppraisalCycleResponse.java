package com.tbcpl.workforce.hr.performance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalCycleType;
import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrAppraisalCycleResponse {

    private Long               id;
    private String             cycleName;
    private AppraisalCycleType cycleType;
    private Integer            appraisalYear;
    private LocalDate          periodStartDate;
    private LocalDate          periodEndDate;
    private LocalDate          selfReviewStartDate;
    private LocalDate          selfReviewEndDate;
    private LocalDate          managerReviewEndDate;
    private AppraisalStatus    status;
    private String             description;
    private String             applicableDepartments;
    private Boolean            isActive;
    private LocalDateTime      createdAt;
    private LocalDateTime      updatedAt;
    private String             createdBy;
}