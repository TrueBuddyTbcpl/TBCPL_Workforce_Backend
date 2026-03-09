package com.tbcpl.workforce.operation.cases.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CreateCaseRequest {

    private String priority;

    private String caseType;

    private String clientEmail;

    private LocalDate estimatedCompletionDate;

    private List<String> assignedEmployees;
}
