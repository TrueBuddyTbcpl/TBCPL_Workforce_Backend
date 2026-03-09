package com.tbcpl.workforce.operation.finalreport.dto.response;

import com.tbcpl.workforce.operation.finalreport.entity.enums.FinalReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalReportListItemResponse {

    private Long id;
    private String reportNumber;
    private Long caseId;
    private String caseNumber;
    private String clientName;
    private String clientLogoUrl;
    private String reportTitle;
    private LocalDate reportDate;
    private FinalReportStatus reportStatus;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
