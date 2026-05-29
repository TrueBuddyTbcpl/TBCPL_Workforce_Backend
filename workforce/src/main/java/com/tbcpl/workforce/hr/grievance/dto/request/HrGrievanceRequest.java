package com.tbcpl.workforce.hr.grievance.dto.request;

import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceCategory;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievancePriority;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrGrievanceRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotNull(message = "Category is required")
    private GrievanceCategory category;

    @NotBlank(message = "Subject is required")
    @Size(min = 5, max = 255, message = "Subject must be between 5 and 255 characters")
    private String subject;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    @Size(max = 500)
    private String attachmentUrl;

    private GrievancePriority priority;

    private Boolean isAnonymous;
}