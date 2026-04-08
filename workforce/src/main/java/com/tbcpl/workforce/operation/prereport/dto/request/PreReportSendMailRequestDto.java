package com.tbcpl.workforce.operation.prereport.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PreReportSendMailRequestDto {

    @NotBlank(message = "Recipient email is required")
    @Email(message = "Valid email address is required")
    private String toEmail;

    @NotBlank(message = "Recipient name is required")
    private String toName;

    @NotBlank(message = "Case title is required")
    private String caseTitle;

    private String notes;
}