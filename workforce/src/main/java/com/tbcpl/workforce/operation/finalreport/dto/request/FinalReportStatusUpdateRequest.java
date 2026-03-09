package com.tbcpl.workforce.operation.finalreport.dto.request;

import com.tbcpl.workforce.operation.finalreport.entity.enums.FinalReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinalReportStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private FinalReportStatus reportStatus;

    private String changeComments;
}
