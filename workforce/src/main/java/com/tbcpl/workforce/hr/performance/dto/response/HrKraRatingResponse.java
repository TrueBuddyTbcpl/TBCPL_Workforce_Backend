package com.tbcpl.workforce.hr.performance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.performance.entity.enums.RatingScale;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrKraRatingResponse {

    private Long        id;
    private Long        kraTemplateId;
    private String      kraName;
    private Double      weightage;
    private String      targetValue;
    private String      measurementUnit;
    private String      achievedValue;
    private RatingScale selfRating;
    private String      selfComments;
    private RatingScale managerRating;
    private String      managerComments;
    private Double      weightedScore;
}