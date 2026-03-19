package com.tbcpl.workforce.operation.prereport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomRiskEntry {
    private Long   optionId;
    private String value;    // RiskLevel string: HIGH / MEDIUM / LOW
}
