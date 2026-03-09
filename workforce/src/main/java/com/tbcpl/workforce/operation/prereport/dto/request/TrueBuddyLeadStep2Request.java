package com.tbcpl.workforce.operation.prereport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep2Request {

    private Boolean scopeIprSupplier;
    private Boolean scopeIprManufacturer;
    private Boolean scopeIprStockist;
    private Boolean scopeMarketVerification;
    private Boolean scopeEtp;
    private Boolean scopeEnforcement;
}
