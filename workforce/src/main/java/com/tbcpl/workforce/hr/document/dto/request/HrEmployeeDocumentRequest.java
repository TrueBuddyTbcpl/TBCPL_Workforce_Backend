package com.tbcpl.workforce.hr.document.dto.request;

import com.tbcpl.workforce.hr.document.entity.enums.DocumentType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrEmployeeDocumentRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document name is required")
    @Size(min = 2, max = 150)
    private String documentName;

    @NotBlank(message = "File URL is required")
    @Size(max = 500)
    private String fileUrl;

    @Size(max = 255)
    private String originalFileName;

    @Size(max = 100)
    private String fileMimeType;

    @Min(value = 1)
    private Long fileSizeKb;

    @Size(max = 50)
    private String documentNumber;

    private LocalDate issueDate;

    private LocalDate expiryDate;

    private Boolean isMandatory;
}