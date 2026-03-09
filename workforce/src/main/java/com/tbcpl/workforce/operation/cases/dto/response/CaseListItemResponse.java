package com.tbcpl.workforce.operation.cases.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CaseListItemResponse {
    private Long id;
    private String caseNumber;
    private String caseTitle;
    private String priority;
    private String status;
    private String caseType;
    private String leadType;
    private String clientName;
    private String clientProduct;
    private LocalDate dateOpened;
    private LocalDate estimatedCompletionDate;
    private String createdBy;
    private LocalDateTime createdAt;
}
