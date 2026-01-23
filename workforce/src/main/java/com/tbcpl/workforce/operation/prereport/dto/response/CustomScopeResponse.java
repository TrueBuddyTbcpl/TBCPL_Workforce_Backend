package com.tbcpl.workforce.operation.prereport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomScopeResponse {

    private Long id;
    private String scopeName;
    private String createdBy;
    private LocalDateTime createdAt;
    private Boolean isActive;
}
