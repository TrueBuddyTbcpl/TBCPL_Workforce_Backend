package com.tbcpl.workforce.operation.prereport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomVerificationEntry {
    private Long   optionId;
    private String status;   // VerificationStatus string value
    private String notes;
}
