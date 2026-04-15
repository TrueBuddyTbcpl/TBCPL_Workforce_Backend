package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SiblingRequest {

    @NotBlank(message = "Sibling name is required")
    private String name;

    // ── Was: SiblingRelationship relationship ────────────────────────────────
    private String relationship;
    private String relationshipOther;
    // ────────────────────────────────────────────────────────────────────────

    private String occupation;
}