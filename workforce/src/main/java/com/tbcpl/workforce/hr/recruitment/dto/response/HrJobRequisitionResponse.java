package com.tbcpl.workforce.hr.recruitment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrJobRequisitionResponse {

    private Long              id;
    private String            requisitionCode;
    private String            jobTitle;
    private String            department;
    private String            designation;
    private Integer           numberOfPositions;
    private Integer           filledPositions;
    private Integer           remainingPositions;
    private String            jobDescription;
    private Integer           requiredExperienceYears;
    private String            requiredSkills;
    private Double            minSalaryBudget;
    private Double            maxSalaryBudget;
    private LocalDate         targetJoiningDate;
    private String            raisedBy;
    private RecruitmentStatus status;
    private String            closureRemarks;
    private Boolean           isActive;
    private LocalDateTime     createdAt;
    private LocalDateTime     updatedAt;
    private String            createdBy;
}