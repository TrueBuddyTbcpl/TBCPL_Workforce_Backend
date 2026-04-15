package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_business_activities", indexes = {
        @Index(name = "idx_business_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileBusinessActivities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private OpProfile profile;

    // ── Was: @Enumerated BusinessEntityStatus / AuthorizationStatus ──────────
    @Column(name = "retailer_status", length = 100)
    private String retailerStatus;

    @Column(name = "retailer_status_other", length = 255)
    private String retailerStatusOther;

    @Column(name = "retailer_type", length = 100)
    private String retailerType;

    @Column(name = "retailer_type_other", length = 255)
    private String retailerTypeOther;

    @Column(name = "retailer_details", columnDefinition = "TEXT")
    private String retailerDetails;

    @Column(name = "supplier_status", length = 100)
    private String supplierStatus;

    @Column(name = "supplier_status_other", length = 255)
    private String supplierStatusOther;

    @Column(name = "supplier_type", length = 100)
    private String supplierType;

    @Column(name = "supplier_type_other", length = 255)
    private String supplierTypeOther;

    @Column(name = "supplier_details", columnDefinition = "TEXT")
    private String supplierDetails;

    @Column(name = "manufacturer_status", length = 100)
    private String manufacturerStatus;

    @Column(name = "manufacturer_status_other", length = 255)
    private String manufacturerStatusOther;

    @Column(name = "manufacturer_type", length = 100)
    private String manufacturerType;

    @Column(name = "manufacturer_type_other", length = 255)
    private String manufacturerTypeOther;

    @Column(name = "manufacturer_details", columnDefinition = "TEXT")
    private String manufacturerDetails;
    // ────────────────────────────────────────────────────────────────────────

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}