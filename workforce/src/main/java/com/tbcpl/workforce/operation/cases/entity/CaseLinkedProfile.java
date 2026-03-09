package com.tbcpl.workforce.operation.cases.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_case_linked_profiles", indexes = {
        @Index(name = "idx_clp_case_id",    columnList = "case_id"),
        @Index(name = "idx_clp_profile_id", columnList = "profile_id"),
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_case_profile", columnNames = {"case_id", "profile_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseLinkedProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case caseEntity;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;   // ✅ No cross-department entity — store ID only

    @Column(name = "profile_number", nullable = false, length = 20)
    private String profileNumber;

    @Column(name = "profile_name", nullable = false, length = 255)
    private String profileName;

    @Column(name = "linked_by", nullable = false, length = 100)
    private String linkedBy;

    @CreationTimestamp
    @Column(name = "linked_at", nullable = false, updatable = false)
    private LocalDateTime linkedAt;
}
