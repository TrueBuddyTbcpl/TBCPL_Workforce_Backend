package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociatedCompanyResponse {
    private Long id;
    private String companyName;

    // ── Was: RelationshipNature relationshipNature ───────────────────────────
    private String relationshipNature;
    private String relationshipNatureOther;
    // ────────────────────────────────────────────────────────────────────────

    private String details;
}