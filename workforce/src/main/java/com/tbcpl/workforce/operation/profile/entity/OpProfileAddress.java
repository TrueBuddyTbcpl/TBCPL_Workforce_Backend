package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_address", indexes = {
        @Index(name = "idx_address_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private OpProfile profile;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "pincode", length = 20)
    private String pincode;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "permanent_same_as_current")
    private Boolean permanentSameAsCurrent;

    @Column(name = "perm_address_line1", length = 255)
    private String permAddressLine1;

    @Column(name = "perm_address_line2", length = 255)
    private String permAddressLine2;

    @Column(name = "perm_city", length = 100)
    private String permCity;

    @Column(name = "perm_state", length = 100)
    private String permState;

    @Column(name = "perm_pincode", length = 20)
    private String permPincode;

    @Column(name = "perm_country", length = 100)
    private String permCountry;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
