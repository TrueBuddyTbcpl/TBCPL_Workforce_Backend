package com.tbcpl.workforce.operation.profile.entity;

import com.tbcpl.workforce.operation.profile.enums.SiblingRelationship;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_siblings", indexes = {
        @Index(name = "idx_sibling_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileSibling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private OpProfile profile;

    @Column(name = "name", length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", length = 20)
    private SiblingRelationship relationship;

    @Column(name = "occupation", length = 255)
    private String occupation;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
