package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_physical_attributes", indexes = {
        @Index(name = "idx_physical_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfilePhysicalAttributes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private OpProfile profile;

    @Column(name = "height", length = 50)
    private String height;

    @Column(name = "weight", length = 50)
    private String weight;

    @Column(name = "eye_color", length = 50)
    private String eyeColor;

    @Column(name = "hair_color", length = 50)
    private String hairColor;

    @Column(name = "skin_tone", length = 50)
    private String skinTone;

    @Column(name = "identification_marks", columnDefinition = "TEXT")
    private String identificationMarks;

    @Column(name = "disabilities", columnDefinition = "TEXT")
    private String disabilities;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
