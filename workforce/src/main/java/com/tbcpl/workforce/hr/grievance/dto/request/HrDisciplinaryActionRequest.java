package com.tbcpl.workforce.hr.grievance.dto.request;

import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryActionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrDisciplinaryActionRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotNull(message = "Action type is required")
    private DisciplinaryActionType actionType;

    @NotBlank(message = "Subject is required")
    @Size(min = 5, max = 255)
    private String subject;

    @NotBlank(message = "Incident description is required")
    @Size(min = 10, max = 5000)
    private String incidentDescription;

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    private Long relatedGrievanceId;

    private LocalDate noticeIssuedDate;

    @Size(max = 500)
    private String noticeDocumentUrl;

    @Size(max = 5000)
    private String employeeResponse;

    private LocalDate employeeResponseDate;

    @Size(max = 5000)
    private String finalDecision;

    private LocalDate actionEffectiveDate;

    private LocalDate actionEndDate;

    @DecimalMin(value = "0.01")
    private Double deductionAmount;
}