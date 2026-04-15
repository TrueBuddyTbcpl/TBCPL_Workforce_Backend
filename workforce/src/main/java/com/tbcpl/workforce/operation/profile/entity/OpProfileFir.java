package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "op_profile_firs", indexes = {
        @Index(name = "idx_fir_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileFir {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private OpProfile profile;

    @Column(name = "fir_number", length = 100)
    private String firNumber;

    @Column(name = "case_number", length = 100)
    private String caseNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sections", columnDefinition = "JSON")
    @Builder.Default
    private List<String> sections = new ArrayList<>();

    @Column(name = "date_registered")
    private LocalDate dateRegistered;

    // ── Was: @Enumerated FirStatus status ────────────────────────────────────
    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "status_other", length = 255)
    private String statusOther;
    // ────────────────────────────────────────────────────────────────────────

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}