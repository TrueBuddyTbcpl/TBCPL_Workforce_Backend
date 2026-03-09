package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.RiskLevel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdditionalInfoRequest {
    private String notes;
    private String behavioralNotes;
    private RiskLevel riskLevel;
    private List<String> tags = new ArrayList<>();
    private List<String> additionalPhotos = new ArrayList<>();
    private List<String> attachments = new ArrayList<>();
    private List<String> linkedCases = new ArrayList<>();
}
