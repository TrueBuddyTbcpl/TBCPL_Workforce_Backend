package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_family_background", indexes = {
        @Index(name = "idx_family_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileFamilyBackground {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private OpProfile profile;

    @Column(name = "father_name", length = 255)
    private String fatherName;

    @Column(name = "father_occupation", length = 255)
    private String fatherOccupation;

    @Column(name = "father_contact", length = 20)
    private String fatherContact;

    @Column(name = "mother_name", length = 255)
    private String motherName;

    @Column(name = "mother_occupation", length = 255)
    private String motherOccupation;

    @Column(name = "mother_contact", length = 20)
    private String motherContact;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
