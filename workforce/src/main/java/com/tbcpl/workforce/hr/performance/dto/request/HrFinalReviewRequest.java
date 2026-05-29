package com.tbcpl.workforce.hr.performance.dto.request;

import com.tbcpl.workforce.hr.performance.entity.enums.RatingScale;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrFinalReviewRequest {

    @NotBlank(message = "HR review comments are required")
    @Size(max = 2000)
    private String hrReviewComments;

    @NotNull(message = "Final rating is required")
    private RatingScale finalRating;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private Double incrementPercentage;
}