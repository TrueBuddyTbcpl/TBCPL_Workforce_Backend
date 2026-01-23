package com.tbcpl.workforce.operation.prereport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReportDetailResponse {

    private PreReportResponse preReport;
    private ClientLeadStepResponse clientLeadData;
    private TrueBuddyLeadStepResponse trueBuddyLeadData;
}
