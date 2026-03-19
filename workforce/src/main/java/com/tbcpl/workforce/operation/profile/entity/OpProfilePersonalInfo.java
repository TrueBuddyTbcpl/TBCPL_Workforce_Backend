package com.tbcpl.workforce.operation.profile.entity;

import com.tbcpl.workforce.operation.profile.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_personal_info", indexes = {
        @Index(name = "idx_personal_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfilePersonalInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private OpProfile profile;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = true, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "nationality", length = 100)
    private String nationality;

    @Column(name = "profile_photo", length = 500)
    private String profilePhoto;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
