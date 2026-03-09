package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "op_profile_geographic_exposure", indexes = {
        @Index(name = "idx_geo_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileGeographicExposure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private OpProfile profile;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "operating_regions", columnDefinition = "JSON")
    @Builder.Default
    private List<String> operatingRegions = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "markets", columnDefinition = "JSON")
    @Builder.Default
    private List<String> markets = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "jurisdictions", columnDefinition = "JSON")
    @Builder.Default
    private List<String> jurisdictions = new ArrayList<>();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
