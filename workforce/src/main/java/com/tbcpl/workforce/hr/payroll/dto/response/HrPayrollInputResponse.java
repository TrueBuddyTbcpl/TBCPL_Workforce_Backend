package com.tbcpl.workforce.hr.payroll.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.payroll.entity.enums.PayrollInputType;
import com.tbcpl.workforce.hr.payroll.entity.enums.PayrollStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrPayrollInputResponse {

    private Long             id;
    private String           empId;
    private Integer          payrollMonth;
    private Integer          payrollYear;
    private PayrollInputType inputType;
    private Double           amount;
    private String           description;
    private Double           lwpDays;
    private PayrollStatus    status;
    private LocalDateTime    submittedAt;
    private String           submittedBy;
    private Boolean          isActive;
    private LocalDateTime    createdAt;
    private LocalDateTime    updatedAt;
    private String           createdBy;
}