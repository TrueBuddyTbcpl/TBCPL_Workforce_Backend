package com.tbcpl.workforce.hr.recruitment.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrOfferLetterRequest {

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotNull(message = "Job Requisition ID is required")
    private Long jobRequisitionId;

    @NotBlank(message = "Designation offered is required")
    @Size(max = 100)
    private String designationOffered;

    @NotBlank(message = "Department offered is required")
    @Size(max = 50)
    private String departmentOffered;

    @NotNull(message = "CTC offered is required")
    @DecimalMin(value = "1000.0", message = "CTC must be at least 1000")
    private Double ctcOffered;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    private LocalDate offerExpiryDate;

    @Size(max = 100)
    private String workLocation;

    @Size(max = 500)
    private String documentUrl;

    @Size(max = 2000)
    private String specialConditions;
}