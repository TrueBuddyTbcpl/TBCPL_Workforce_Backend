package com.tbcpl.workforce.operation.finalreport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseReportPrefillResponse {

    private Long caseId;
    private String caseNumber;
    private Long clientId;
    private String clientName;
    private String clientLogoUrl;
    private boolean reportAlreadyExists;
    private Long existingReportId; // null if no report yet
}
