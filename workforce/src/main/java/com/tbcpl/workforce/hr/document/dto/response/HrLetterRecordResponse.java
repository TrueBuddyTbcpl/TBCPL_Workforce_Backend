package com.tbcpl.workforce.hr.document.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.document.entity.enums.LetterType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrLetterRecordResponse {

    private Long          id;
    private String        referenceNumber;
    private String        empId;
    private LetterType    letterType;
    private String        subject;
    private String        content;
    private String        fileUrl;
    private LocalDate     issuedDate;
    private String        issuedBy;
    private Boolean       isAcknowledged;
    private LocalDateTime acknowledgedAt;
    private String        remarks;
    private Boolean       isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        createdBy;
}