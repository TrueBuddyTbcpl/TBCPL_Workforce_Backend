package com.tbcpl.workforce.operation.cases.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CaseDocumentResponse {
    private Long id;
    private String fileName;
    private String originalName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
}
