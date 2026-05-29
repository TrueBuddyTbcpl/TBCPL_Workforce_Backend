package com.tbcpl.workforce.hr.recruitment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.recruitment.entity.enums.CandidateSource;
import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferStatus;
import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrCandidateResponse {

    private Long              id;
    private Long              jobRequisitionId;
    private String            requisitionCode;
    private String            jobTitle;
    private String            fullName;
    private String            email;
    private String            phone;
    private String            currentCompany;
    private String            currentDesignation;
    private Double            totalExperienceYears;
    private Double            currentCtc;
    private Double            expectedCtc;
    private Integer           noticePeriodDays;
    private String            resumeUrl;
    private String            linkedinUrl;
    private CandidateSource   source;
    private String            referredBy;
    private RecruitmentStatus status;
    private OfferStatus       offerStatus;
    private Double            offeredCtc;
    private LocalDate         offerDate;
    private LocalDate         expectedJoiningDate;
    private String            empId;
    private String            rejectionReason;
    private String            hrRemarks;
    private Boolean           isActive;
    private LocalDateTime     createdAt;
    private LocalDateTime     updatedAt;
    private String            createdBy;
}