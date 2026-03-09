package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReportInitializeRequest {

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotEmpty(message = "At least one product must be selected")
    private List<Long> productIds;

    @NotNull(message = "Lead type is required")
    private LeadType leadType;
}
