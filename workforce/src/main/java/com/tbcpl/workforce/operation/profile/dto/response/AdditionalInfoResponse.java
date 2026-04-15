package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInfoResponse {
    private String notes;
    private String behavioralNotes;

    // ── Was: RiskLevel riskLevel ─────────────────────────────────────────────
    private String riskLevel;
    private String riskLevelOther;
    // ────────────────────────────────────────────────────────────────────────

    private List<String> tags;
    private List<String> additionalPhotos;
    private List<String> attachments;
    private List<String> linkedCases;
}