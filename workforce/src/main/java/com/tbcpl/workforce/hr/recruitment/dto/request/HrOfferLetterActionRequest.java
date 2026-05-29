package com.tbcpl.workforce.hr.recruitment.dto.request;

import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferLetterStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrOfferLetterActionRequest {

    @NotNull(message = "Status is required")
    private OfferLetterStatus status;

    @Size(max = 1000)
    private String candidateRemarks;

    // Updated document URL if regenerated
    @Size(max = 500)
    private String documentUrl;
}