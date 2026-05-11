package com.tbcpl.workforce.ttr.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtrChildCreateRequest {

    @NotBlank(message = "Assigned employee empId is required")
    private String assignedEmpId;

    @NotBlank(message = "Notes are required")
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}