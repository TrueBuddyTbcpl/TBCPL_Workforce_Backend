package com.tbcpl.workforce.hr.payroll.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.payroll.entity.enums.ComponentCalculationType;
import com.tbcpl.workforce.hr.payroll.entity.enums.SalaryComponentType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrSalaryComponentResponse {

    private Long                    id;
    private String                  componentName;
    private SalaryComponentType     componentType;
    private ComponentCalculationType calculationType;
    private Double                  percentageValue;
    private Double                  flatAmount;
    private Double                  monthlyAmount;
    private Double                  annualAmount;
    private Boolean                 isStatutory;
    private Integer                 displayOrder;
    private Boolean                 isActive;
}