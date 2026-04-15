package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_emergency_contacts", indexes = {
        @Index(name = "idx_emergency_contact_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileEmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private OpProfile profile;

    @Column(name = "name", length = 150)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "relation", length = 100)
    private String relation;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}