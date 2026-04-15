package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialSeizedItemResponse {
    private Long id;
    private String brandName;
    private String company;
    private String quantity;
    private String location;

    // ── Was: RaidingAuthority raidingAuthority ───────────────────────────────
    private String raidingAuthority;
    private String raidingAuthorityOther;
    // ────────────────────────────────────────────────────────────────────────

    private LocalDate dateSeized;
}