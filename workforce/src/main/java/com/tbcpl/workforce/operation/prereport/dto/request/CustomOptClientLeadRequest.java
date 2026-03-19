package com.tbcpl.workforce.operation.prereport.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomOptClientLeadRequest {

    @NotNull(message = "Step number is required")
    @Min(value = 2, message = "Step number must be 2, 4, 5 or 8")
    @Max(value = 8, message = "Step number must be 2, 4, 5 or 8")
    private Integer stepNumber;

    @NotBlank(message = "Option name is required")
    @Size(max = 255, message = "Option name cannot exceed 255 characters")
    private String optionName;

    @NotBlank(message = "Lead type is required")
    private String leadType;

    private String optionDescription;
}
