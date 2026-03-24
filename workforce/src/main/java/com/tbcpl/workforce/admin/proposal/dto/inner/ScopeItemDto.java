package com.tbcpl.workforce.admin.proposal.dto.inner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScopeItemDto {
    private String key;
    private String label;
    private Boolean isPredefined;
    private Boolean selected;
}
