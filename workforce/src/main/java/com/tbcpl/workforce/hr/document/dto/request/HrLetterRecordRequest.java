package com.tbcpl.workforce.hr.document.dto.request;

import com.tbcpl.workforce.hr.document.entity.enums.LetterType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrLetterRecordRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotNull(message = "Letter type is required")
    private LetterType letterType;

    @NotBlank(message = "Subject is required")
    @Size(min = 3, max = 255)
    private String subject;

    // HTML or plain-text letter body
    private String content;

    // Optional — URL if already generated/uploaded as PDF
    @Size(max = 500)
    private String fileUrl;

    @NotNull(message = "Issued date is required")
    private LocalDate issuedDate;

    @Size(max = 500)
    private String remarks;
}