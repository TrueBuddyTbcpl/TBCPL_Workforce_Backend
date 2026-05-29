package com.tbcpl.workforce.hr.recruitment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferLetterStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrOfferLetterResponse {

    private Long              id;
    private Long              candidateId;
    private String            candidateName;
    private String            candidateEmail;
    private Long              jobRequisitionId;
    private String            jobTitle;
    private String            designationOffered;
    private String            departmentOffered;
    private Double            ctcOffered;
    private LocalDate         joiningDate;
    private LocalDate         offerExpiryDate;
    private String            workLocation;
    private String            documentUrl;
    private String            specialConditions;
    private String            candidateRemarks;
    private LocalDateTime     candidateRespondedAt;
    private OfferLetterStatus status;
    private Boolean           isActive;
    private LocalDateTime     createdAt;
    private LocalDateTime     updatedAt;
    private String            createdBy;
}