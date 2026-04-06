// com.tbcpl.workforce.admin.dto.request.SendReportEmailRequest.java
package com.tbcpl.workforce.admin.dto.request;

import com.tbcpl.workforce.common.enums.ReportType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendReportEmailRequest {

    @NotNull(message = "Report type is required")
    private ReportType reportType;

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid recipient email format")
    private String toEmail;

    @NotBlank(message = "Recipient name is required")
    private String toName;

    @NotBlank(message = "Client name is required")
    private String clientName;

    @NotBlank(message = "Case reference is required")
    private String caseReference;

    @NotBlank(message = "Prepared by is required")
    private String preparedBy;

    @NotBlank(message = "Report date is required")
    private String reportDate;
}