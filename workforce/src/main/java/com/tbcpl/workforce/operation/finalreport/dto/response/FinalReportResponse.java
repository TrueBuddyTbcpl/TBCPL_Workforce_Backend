package com.tbcpl.workforce.operation.finalreport.dto.response;

import com.tbcpl.workforce.operation.finalreport.entity.enums.FinalReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tbcpl.workforce.operation.finalreport.entity.json.SectionData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalReportResponse {

    private Long id;
    private String reportNumber;

    // Case
    private Long caseId;
    private String caseNumber;

    // Client
    private Long clientId;
    private String clientName;
    private String clientLogoUrl;

    // Header
    private String reportTitle;
    private String reportSubtitle;
    private String preparedFor;
    private String preparedBy;
    private LocalDate reportDate;

    // Content
    private List<SectionData> sections;
    private List<String> tableOfContents;

    private Map<String, Object> photographicEvidence;

    // Status
    private FinalReportStatus reportStatus;
    private String changeComments;

    // Computed permission flags (for frontend button visibility)
    private boolean previewEnabled;     // WAITING_FOR_APPROVAL | REQUEST_CHANGES | APPROVED
    private boolean sendReportEnabled;  // APPROVED only (admin)
    private boolean generatePdfEnabled; // APPROVED only (admin)

    // Audit
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
