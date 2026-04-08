package com.tbcpl.workforce.operation.cases.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCaseUpdateRequest {
    private String description;

    // ── NEW ────────────────────────────────────────────────────────────
    private String procedureDoneBy;   // Full name of the employee who performed the procedure

    // ──────────────────────────────────────────────────────────────────
    private String procedureDoneByEmpId;

}