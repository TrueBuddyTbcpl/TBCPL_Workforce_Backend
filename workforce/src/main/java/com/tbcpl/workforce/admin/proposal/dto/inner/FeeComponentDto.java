package com.tbcpl.workforce.admin.proposal.dto.inner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeeComponentDto {
    private String type;       // TEST_PURCHASE_FEE, SAMPLE_COST, SHIPPING
    private String label;
    private BigDecimal amount; // null when isActuals = true
    private Boolean isActuals;
}
