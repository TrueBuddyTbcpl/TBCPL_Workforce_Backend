package com.tbcpl.workforce.operation.prereport.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomScopeCreateRequest {

    @NotBlank(message = "Scope name is required")
    private String scopeName;
}
