package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalInfoResponse {
    private String notes;
    private String behavioralNotes;
    private RiskLevel riskLevel;
    private List<String> tags;
    private List<String> additionalPhotos;
    private List<String> attachments;
    private List<String> linkedCases;
}
