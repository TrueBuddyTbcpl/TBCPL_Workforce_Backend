package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_vehicles", indexes = {
        @Index(name = "idx_vehicle_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileVehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private OpProfile profile;

    @Column(name = "make", length = 100)
    private String make;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "registration_number", length = 50)
    private String registrationNumber;

    // ── Was: @Enumerated VehicleOwnershipType ownershipType ──────────────────
    @Column(name = "ownership_type", length = 100)
    private String ownershipType;

    @Column(name = "ownership_type_other", length = 255)
    private String ownershipTypeOther;
    // ────────────────────────────────────────────────────────────────────────

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}