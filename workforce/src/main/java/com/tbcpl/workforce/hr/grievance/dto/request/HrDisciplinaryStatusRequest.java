package com.tbcpl.workforce.hr.grievance.dto.request;

import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrDisciplinaryStatusRequest {

    @NotNull(message = "Status is required")
    private DisciplinaryStatus status;

    @Size(max = 5000)
    private String finalDecision;

    private LocalDate actionEffectiveDate;

    private LocalDate actionEndDate;

    @DecimalMin(value = "0.01")
    private Double deductionAmount;

    @Size(max = 500)
    private String noticeDocumentUrl;

    private LocalDate noticeIssuedDate;

    @Size(max = 5000)
    private String employeeResponse;

    private LocalDate employeeResponseDate;
}