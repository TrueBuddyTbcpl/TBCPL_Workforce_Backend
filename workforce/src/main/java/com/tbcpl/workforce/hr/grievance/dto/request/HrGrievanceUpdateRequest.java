package com.tbcpl.workforce.hr.grievance.dto.request;

import com.tbcpl.workforce.hr.grievance.entity.enums.GrievancePriority;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrGrievanceUpdateRequest {

    private GrievanceStatus   status;
    private GrievancePriority priority;

    @Size(max = 100)
    private String assignedTo;

    @Size(max = 2000)
    private String resolutionRemarks;
}