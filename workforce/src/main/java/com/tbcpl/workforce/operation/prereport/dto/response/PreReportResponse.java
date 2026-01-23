package com.tbcpl.workforce.operation.prereport.dto.response;

import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReportResponse {

    private Long id;
    private String reportId;
    private Long clientId;
    private String clientName;
    private List<Long> productIds;
    private List<String> productNames;
    private LeadType leadType;
    private ReportStatus reportStatus;
    private Integer currentStep;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
