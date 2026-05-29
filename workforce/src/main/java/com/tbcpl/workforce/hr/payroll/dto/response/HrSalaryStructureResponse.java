package com.tbcpl.workforce.hr.payroll.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrSalaryStructureResponse {

    private Long                           id;
    private String                         empId;
    private Double                         annualCtc;
    private Double                         monthlyGross;
    private LocalDate                      effectiveFrom;
    private LocalDate                      effectiveTo;
    private String                         revisionRemarks;
    private Boolean                        isActive;

    // Breakdown
    private List<HrSalaryComponentResponse> components;

    // Computed summary
    private Double totalMonthlyEarnings;
    private Double totalMonthlyDeductions;
    private Double netMonthlySalary;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        createdBy;
}