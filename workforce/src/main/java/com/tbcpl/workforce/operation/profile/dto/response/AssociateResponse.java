package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssociateResponse {
    private Long id;
    private String name;
    private String relationship;

    // ── Was: AssociateRole role ──────────────────────────────────────────────
    private String role;
    private String roleOther;
    // ────────────────────────────────────────────────────────────────────────

    private String contactInfo;
    private String notes;
}