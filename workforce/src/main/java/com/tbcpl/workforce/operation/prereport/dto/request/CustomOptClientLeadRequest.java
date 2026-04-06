package com.tbcpl.workforce.operation.prereport.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomOptClientLeadRequest {

    @NotNull(message = "Step number is required")
    @Min(value = 1, message = "Step number must be at least 1")
    @Max(value = 11, message = "Step number must not exceed 11")
    private Integer stepNumber;

    @NotBlank(message = "Option name is required")
    @Size(max = 255, message = "Option name cannot exceed 255 characters")
    private String optionName;

    @NotBlank(message = "Lead type is required")
    private String leadType;

    private String optionDescription;

    // ← ADD: optional — used when option belongs to a specific field
    // e.g. "PRODUCT_CATEGORY", "INTELLIGENCE_NATURE"
    private String fieldKey;
}