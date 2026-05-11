package com.tbcpl.workforce.ttr.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TtrCompletionRecordResponse {

    private Long          id;
    private Long          ttrId;
    private String        ttrNumber;
    private Integer       cycleNumber;
    private String        completedByEmpId;
    private String        completedByName;
    private LocalDateTime completedAt;
    private String        proofFileUrl;
    private String        proofFileName;
    private String        notes;
}