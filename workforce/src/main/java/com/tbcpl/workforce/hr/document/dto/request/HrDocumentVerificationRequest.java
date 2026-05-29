package com.tbcpl.workforce.hr.document.dto.request;

import com.tbcpl.workforce.hr.document.entity.enums.DocumentStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrDocumentVerificationRequest {

    @NotNull(message = "Verification status is required")
    private DocumentStatus status;

    @Size(max = 500, message = "Remarks cannot exceed 500 characters")
    private String verificationRemarks;
}