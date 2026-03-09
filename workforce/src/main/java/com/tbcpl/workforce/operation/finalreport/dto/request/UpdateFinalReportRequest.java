package com.tbcpl.workforce.operation.finalreport.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateFinalReportRequest {

    @NotBlank(message = "Report title is required")
    private String reportTitle;

    @NotBlank(message = "Report subtitle is required")
    private String reportSubtitle;

    @NotBlank(message = "Prepared for is required")
    private String preparedFor;

    @NotBlank(message = "Prepared by is required")
    private String preparedBy;

    @NotNull(message = "Report date is required")
    private LocalDate reportDate;

    @NotNull(message = "Sections are required")
    private List<Object> sections;

    private List<String> tableOfContents;
}
