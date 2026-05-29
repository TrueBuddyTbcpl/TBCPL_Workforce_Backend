package com.tbcpl.workforce.hr.recruitment.dto.request;

import com.tbcpl.workforce.hr.recruitment.entity.enums.CandidateSource;
import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrCandidateRequest {

    @NotNull(message = "Job requisition ID is required")
    private Long jobRequisitionId;

    @NotBlank(message = "Candidate full name is required")
    @Size(min = 2, max = 150)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String phone;

    @Size(max = 150)
    private String currentCompany;

    @Size(max = 100)
    private String currentDesignation;

    @DecimalMin(value = "0.0")
    private Double totalExperienceYears;

    @DecimalMin(value = "0.0")
    private Double currentCtc;

    @DecimalMin(value = "0.0")
    private Double expectedCtc;

    @Min(value = 0)
    @Max(value = 365)
    private Integer noticePeriodDays;

    @Size(max = 500)
    private String resumeUrl;

    @Size(max = 255)
    private String linkedinUrl;

    private CandidateSource source;

    @Size(max = 100)
    private String referredBy;

    @Size(max = 500)
    private String hrRemarks;

    // For offer update
    private OfferStatus offerStatus;

    @DecimalMin(value = "0.0")
    private Double offeredCtc;

    private LocalDate offerDate;

    private LocalDate expectedJoiningDate;

    // Set when candidate joins
    @Size(max = 20)
    private String empId;

    @Size(max = 500)
    private String rejectionReason;
}