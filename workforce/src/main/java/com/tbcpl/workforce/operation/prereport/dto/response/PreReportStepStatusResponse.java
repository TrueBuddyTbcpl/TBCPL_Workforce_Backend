package com.tbcpl.workforce.operation.prereport.dto.response;

import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReportStepStatusResponse {

    private Long prereportId;
    private String reportId;
    private LeadType leadType;
    private ReportStatus reportStatus;
    private Integer currentStep;
    private Boolean canEdit;
    private String changeComments;
    private String rejectionReason;
    private List<StepStatusDetail> steps;
}
