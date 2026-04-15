package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiblingResponse {
    private Long id;
    private String name;

    // ── Was: SiblingRelationship relationship ────────────────────────────────
    private String relationship;
    private String relationshipOther;
    // ────────────────────────────────────────────────────────────────────────

    private String occupation;
}