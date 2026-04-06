// com.tbcpl.workforce.common.dto.ReportEmailRequestDto.java
package com.tbcpl.workforce.common.dto;

import com.tbcpl.workforce.common.enums.ReportType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportEmailRequestDto {

    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid recipient email")
    private String toEmail;

    @NotBlank(message = "Recipient name is required")
    private String toName;

    // Client/case context shown in the email body
    private String clientName;
    private String caseReference;
    private String preparedBy;
    private String reportDate;

    // The actual PDF bytes
    @NotNull(message = "PDF attachment is required")
    private byte[] pdfBytes;

    @NotBlank(message = "PDF file name is required")
    private String pdfFileName;
}