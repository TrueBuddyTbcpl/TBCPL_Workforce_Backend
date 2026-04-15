package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private Long id;
    private String make;
    private String model;
    private String registrationNumber;

    // ── Was: VehicleOwnershipType ownershipType ──────────────────────────────
    private String ownershipType;
    private String ownershipTypeOther;
    // ────────────────────────────────────────────────────────────────────────
}