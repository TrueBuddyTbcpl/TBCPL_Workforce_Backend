package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.ProfileStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSummaryResponse {
    private Long id;
    private String profileNumber;
    private String name;
    private String profilePhoto;
    private ProfileStatus status;        // system-controlled — stays enum ✅

    // ── Was: RiskLevel riskLevel ─────────────────────────────────────────────
    private String riskLevel;            // now String
    // ────────────────────────────────────────────────────────────────────────

    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long completedSteps;
    private long totalSteps;
}