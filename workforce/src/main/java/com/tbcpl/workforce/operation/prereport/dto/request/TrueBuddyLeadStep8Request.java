package com.tbcpl.workforce.operation.prereport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep8Request {

    private Boolean recCovertValidation;
    private Boolean recEtp;
    private Boolean recMarketReconnaissance;
    private Boolean recEnforcementDeferred;
    private Boolean recContinuedMonitoring;
    private Boolean recClientSegregation;
    private List<Long> recCustomIds;
}
