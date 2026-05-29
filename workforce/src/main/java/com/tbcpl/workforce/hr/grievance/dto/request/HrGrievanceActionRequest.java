package com.tbcpl.workforce.hr.grievance.dto.request;

import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrGrievanceActionRequest {

    @NotNull(message = "Action status is required")
    private GrievanceStatus status;

    // Required when assigning (UNDER_REVIEW)
    @Size(max = 100)
    private String assignedTo;

    // Required when closing or rejecting
    @Size(min = 5, max = 2000, message = "Action remarks must be at least 5 characters")
    private String actionRemarks;

    // Optional: escalate to senior HR
    @Size(max = 100)
    private String escalatedTo;
}