package com.tbcpl.workforce.hr.performance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalStatus;
import com.tbcpl.workforce.hr.performance.entity.enums.RatingScale;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrEmployeeAppraisalResponse {

    private Long                      id;
    private String                    empId;
    private Long                      cycleId;
    private String                    cycleName;
    private String                    cycleType;
    private String                    managerEmpId;

    // Self review
    private String                    selfReviewComments;
    private LocalDateTime             selfReviewSubmittedAt;
    private RatingScale               selfRating;

    // Manager review
    private String                    managerReviewComments;
    private LocalDateTime             managerReviewSubmittedAt;
    private RatingScale               managerRating;

    // HR / Final review
    private String                    hrReviewComments;
    private LocalDateTime             hrReviewSubmittedAt;
    private RatingScale               finalRating;
    private Double                    finalScore;
    private Double                    incrementPercentage;

    private AppraisalStatus           status;
    private List<HrKraRatingResponse> kraRatings;
    private Boolean                   isActive;
    private LocalDateTime             createdAt;
    private LocalDateTime             updatedAt;
    private String                    createdBy;
}