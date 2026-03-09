package com.tbcpl.workforce.operation.profile.entity;

import com.tbcpl.workforce.operation.profile.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "op_profile_additional_info", indexes = {
        @Index(name = "idx_additional_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileAdditionalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private OpProfile profile;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "behavioral_notes", columnDefinition = "TEXT")
    private String behavioralNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "JSON")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "additional_photos", columnDefinition = "JSON")
    @Builder.Default
    private List<String> additionalPhotos = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attachments", columnDefinition = "JSON")
    @Builder.Default
    private List<String> attachments = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "linked_cases", columnDefinition = "JSON")
    @Builder.Default
    private List<String> linkedCases = new ArrayList<>();

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
