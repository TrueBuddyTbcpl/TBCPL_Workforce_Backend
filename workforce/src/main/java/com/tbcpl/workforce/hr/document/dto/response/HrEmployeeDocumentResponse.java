package com.tbcpl.workforce.hr.document.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.document.entity.enums.DocumentStatus;
import com.tbcpl.workforce.hr.document.entity.enums.DocumentType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrEmployeeDocumentResponse {

    private Long           id;
    private String         empId;
    private DocumentType   documentType;
    private String         documentName;
    private String         fileUrl;
    private String         originalFileName;
    private String         fileMimeType;
    private Long           fileSizeKb;
    private String         documentNumber;
    private LocalDate      issueDate;
    private LocalDate      expiryDate;
    private DocumentStatus status;
    private String         verificationRemarks;
    private String         verifiedBy;
    private LocalDateTime  verifiedAt;
    private Boolean        isMandatory;
    private Boolean        isActive;
    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
    private String         createdBy;
}