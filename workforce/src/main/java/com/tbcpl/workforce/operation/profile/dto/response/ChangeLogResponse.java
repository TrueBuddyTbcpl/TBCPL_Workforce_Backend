package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.ChangeAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogResponse {
    private Long id;
    private String changedBy;
    private String changedByName;
    private LocalDateTime changedAt;
    private String stepName;
    private ChangeAction action;
    private String fieldName;
    private String oldValue;
    private String newValue;
}
