package com.tbcpl.workforce.operation.cases.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CaseUpdateResponse {
    private Long id;
    private LocalDateTime updateDate;
    private String updatedBy;
    private String procedureDoneBy;   // ← NEW
    private String description;
}