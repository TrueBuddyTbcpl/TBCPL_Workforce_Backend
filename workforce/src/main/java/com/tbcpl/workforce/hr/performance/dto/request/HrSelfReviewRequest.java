package com.tbcpl.workforce.hr.performance.dto.request;

import com.tbcpl.workforce.hr.performance.entity.enums.RatingScale;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrSelfReviewRequest {

    @NotBlank(message = "Self review comments are required")
    @Size(max = 2000)
    private String selfReviewComments;

    @NotNull(message = "Self rating is required")
    private RatingScale selfRating;

    @NotEmpty(message = "KRA ratings are required")
    private List<HrKraRatingRequest> kraRatings;
}