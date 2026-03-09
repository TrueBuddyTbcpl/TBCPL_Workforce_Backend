package com.tbcpl.workforce.operation.prereport.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreReportRejectRequest {

    @NotBlank(message = "Rejection reason is required")
    private String rejectionReason;
}
