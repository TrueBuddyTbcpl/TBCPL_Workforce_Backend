package com.tbcpl.workforce.operation.prereport.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomOptClientLeadResponse {
    private Long          id;
    private Integer       stepNumber;
    private String        optionName;
    private String        optionDescription;
    private String        createdBy;
    private LocalDateTime createdAt;
}
