package com.tbcpl.workforce.hr.grievance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceCategory;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievancePriority;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrGrievanceResponse {

    private Long                          id;
    private String                        ticketNumber;
    private String                        empId;
    private GrievanceCategory             category;
    private String                        subject;
    private String                        description;
    private String                        attachmentUrl;
    private GrievancePriority             priority;
    private GrievanceStatus               status;
    private String                        assignedTo;
    private LocalDateTime                 assignedAt;
    private String                        resolutionRemarks;
    private LocalDateTime                 resolvedAt;
    private String                        resolvedBy;
    private Boolean                       isAnonymous;
    private List<HrGrievanceRemarkResponse> remarks;
    private Boolean                       isActive;
    private LocalDateTime                 createdAt;
    private LocalDateTime                 updatedAt;
    private String                        createdBy;
}