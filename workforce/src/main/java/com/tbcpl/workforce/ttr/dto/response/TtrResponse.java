package com.tbcpl.workforce.ttr.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.ttr.entity.enums.TtrModuleType;
import com.tbcpl.workforce.ttr.entity.enums.TtrStatus;
import com.tbcpl.workforce.ttr.entity.enums.TtrType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TtrResponse {

    private Long   id;
    private String ttrNumber;

    private TtrType ttrType;
    private String    ttrTypeDisplayName;

    // Department
    private Long   departmentId;
    private String departmentName;

    // Assigned employee
    private String assignedEmpId;
    private String assignedEmpName;

    // Module
    private TtrModuleType moduleType;
    private String        moduleDisplayName;
    private Long          linkedItemId;
    private String        linkedItemDisplay;

    // Content & status
    private String    notes;
    private TtrStatus status;
    private String    statusDisplayName;

    // Hierarchy
    private Integer nestingDepth;
    private String  parentTtrNumber;        // null if root

    // Children (populated in detail view only)
    private List<TtrResponse> children;

    // Status history (populated in detail view only)
    private List<TtrStatusHistoryResponse> statusHistory;

    // ── ADD after statusHistory ────────────────────────────────────────────────
    private Integer                          totalCompletionCount;   // RECURRING only
    private List<TtrCompletionRecordResponse> completionRecords;     // RECURRING detail view only

    // Audit
    private String        createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}