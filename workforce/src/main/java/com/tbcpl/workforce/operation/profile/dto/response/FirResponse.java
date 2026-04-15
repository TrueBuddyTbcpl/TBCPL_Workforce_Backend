package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirResponse {
    private Long id;
    private String firNumber;
    private String caseNumber;
    private List<String> sections;
    private LocalDate dateRegistered;

    // ── Was: FirStatus status ────────────────────────────────────────────────
    private String status;
    private String statusOther;
    // ────────────────────────────────────────────────────────────────────────
}