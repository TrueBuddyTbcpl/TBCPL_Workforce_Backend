package com.tbcpl.workforce.hr.performance.dto.request;

import com.tbcpl.workforce.hr.performance.entity.enums.RatingScale;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrManagerReviewRequest {

    @NotBlank(message = "Manager review comments are required")
    @Size(max = 2000)
    private String managerReviewComments;

    @NotNull(message = "Manager rating is required")
    private RatingScale managerRating;

    @NotEmpty(message = "KRA ratings are required")
    private List<HrKraRatingRequest> kraRatings;
}