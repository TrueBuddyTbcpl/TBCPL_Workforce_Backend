package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.ProfileStatus;
import com.tbcpl.workforce.operation.profile.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Used in paginated list — lightweight response
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSummaryResponse {
    private Long id;
    private String profileNumber;
    private String name;
    private String profilePhoto;
    private ProfileStatus status;
    private RiskLevel riskLevel;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long completedSteps;
    private long totalSteps;
}
