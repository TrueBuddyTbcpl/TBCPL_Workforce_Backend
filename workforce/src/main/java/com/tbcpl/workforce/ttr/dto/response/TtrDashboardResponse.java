package com.tbcpl.workforce.ttr.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TtrDashboardResponse {

    private String departmentName;
    private Long   total;
    private Long   opened;        // S1
    private Long   inProgress;    // S2
    private Long   completed;     // S3
    private Long   changesRequested; // S4
    private Long   closed;        // S5
}