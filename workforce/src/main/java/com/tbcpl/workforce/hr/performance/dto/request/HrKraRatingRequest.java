package com.tbcpl.workforce.hr.performance.dto.request;

import com.tbcpl.workforce.hr.performance.entity.enums.RatingScale;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrKraRatingRequest {

    @NotNull(message = "KRA template ID is required")
    private Long kraTemplateId;

    @Size(max = 100)
    private String achievedValue;

    private RatingScale selfRating;

    @Size(max = 500)
    private String selfComments;

    // Filled by manager
    private RatingScale managerRating;

    @Size(max = 500)
    private String managerComments;
}