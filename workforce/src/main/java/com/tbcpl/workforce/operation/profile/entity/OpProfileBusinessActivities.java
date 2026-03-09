package com.tbcpl.workforce.operation.profile.entity;

import com.tbcpl.workforce.operation.profile.enums.AuthorizationStatus;
import com.tbcpl.workforce.operation.profile.enums.BusinessEntityStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "retailer_status", length = 20)
    private BusinessEntityStatus retailerStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "retailer_type", length = 20)
    private AuthorizationStatus retailerType;

    @Column(name = "retailer_details", columnDefinition = "TEXT")
    private String retailerDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_status", length = 20)
    private BusinessEntityStatus supplierStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_type", length = 20)
    private AuthorizationStatus supplierType;

    @Column(name = "supplier_details", columnDefinition = "TEXT")
    private String supplierDetails;

    @Enumerated(EnumType.STRING)
    @Column(name = "manufacturer_status", length = 20)
    private BusinessEntityStatus manufacturerStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "manufacturer_type", length = 20)
    private AuthorizationStatus manufacturerType;

    @Column(name = "manufacturer_details", columnDefinition = "TEXT")
    private String manufacturerDetails;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
