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
public class ClientLeadStep2Request {

    private Boolean scopeDueDiligence;
    private Boolean scopeIprRetailer;
    private Boolean scopeIprSupplier;
    private Boolean scopeIprManufacturer;
    private Boolean scopeOnlinePurchase;
    private Boolean scopeOfflinePurchase;
    private List<Long> scopeCustomIds;
}
