package com.tbcpl.workforce.hr.grievance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryActionType;
import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrDisciplinaryActionResponse {

    private Long                   id;
    private String                 caseReference;
    private String                 empId;
    private DisciplinaryActionType actionType;
    private String                 subject;
    private String                 incidentDescription;
    private LocalDate              incidentDate;
    private Long                   relatedGrievanceId;
    private LocalDate              noticeIssuedDate;
    private String                 noticeDocumentUrl;
    private String                 employeeResponse;
    private LocalDate              employeeResponseDate;
    private String                 finalDecision;
    private LocalDate              actionEffectiveDate;
    private LocalDate              actionEndDate;
    private Double                 deductionAmount;
    private String                 initiatedBy;
    private DisciplinaryStatus     status;
    private Boolean                isActive;
    private LocalDateTime          createdAt;
    private LocalDateTime          updatedAt;
    private String                 createdBy;

    // Active case count for this employee — context field
    private Long                   activeCaseCount;
}