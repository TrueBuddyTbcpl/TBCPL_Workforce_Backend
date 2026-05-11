package com.tbcpl.workforce.ttr.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.ttr.entity.enums.TtrStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TtrStatusHistoryResponse {

    private Long      id;
    private TtrStatus oldStatus;
    private TtrStatus newStatus;
    private String    changedBy;
    private String    changedByName;
    private String    comments;
    private LocalDateTime changedAt;
    // Add after changedAt:

    private String proofFileUrl;
    private String proofFileName;
}