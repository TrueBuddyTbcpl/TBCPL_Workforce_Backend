package com.tbcpl.workforce.operation.profile.entity;

import com.tbcpl.workforce.operation.profile.enums.RaidingAuthority;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_material_seized", indexes = {
        @Index(name = "idx_material_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileMaterialSeized {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private OpProfile profile;

    @Column(name = "brand_name", length = 255)
    private String brandName;

    @Column(name = "company", length = 255)
    private String company;

    @Column(name = "quantity", length = 100)
    private String quantity;

    @Column(name = "location", length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "raiding_authority", length = 20)
    private RaidingAuthority raidingAuthority;

    @Column(name = "raiding_authority_other", length = 100)
    private String raidingAuthorityOther;

    @Column(name = "date_seized")
    private LocalDate dateSeized;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
