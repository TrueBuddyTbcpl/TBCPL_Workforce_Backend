package com.tbcpl.workforce.hr.grievance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrGrievanceRemarkResponse {

    private Long          id;
    private String        remarkedBy;
    private String        remarkedByRole;
    private String        remark;
    private Boolean       isInternal;
    private LocalDateTime createdAt;
}