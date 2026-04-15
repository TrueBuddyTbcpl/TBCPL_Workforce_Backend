package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_associates", indexes = {
        @Index(name = "idx_associate_profile_id", columnList = "profile_id"),
        @Index(name = "idx_associate_role",        columnList = "role")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileAssociate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private OpProfile profile;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "relationship", length = 100)
    private String relationship;

    // ── Was: @Enumerated AssociateRole role ─────────────────────────────────
    @Column(name = "role", length = 100)
    private String role;

    @Column(name = "role_other", length = 255)
    private String roleOther;
    // ────────────────────────────────────────────────────────────────────────

    @Column(name = "contact_info", length = 255)
    private String contactInfo;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}