package com.tbcpl.workforce.operation.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_profile_identification_docs", indexes = {
        @Index(name = "idx_id_docs_profile_id", columnList = "profile_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpProfileIdentificationDocs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private OpProfile profile;

    @Column(name = "employee_id", length = 100)
    private String employeeId;

    @Column(name = "aadhaar_number", length = 20)
    private String aadhaarNumber;

    @Column(name = "aadhaar_photo", length = 500)
    private String aadhaarPhoto;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "pan_photo", length = 500)
    private String panPhoto;

    @Column(name = "driving_license", length = 50)
    private String drivingLicense;

    @Column(name = "dl_photo", length = 500)
    private String dlPhoto;

    @Column(name = "passport_number", length = 50)
    private String passportNumber;

    @Column(name = "passport_photo", length = 500)
    private String passportPhoto;

    @Column(name = "other_id_type", length = 100)
    private String otherIdType;

    @Column(name = "other_id_number", length = 100)
    private String otherIdNumber;

    @Column(name = "other_id_photo", length = 500)
    private String otherIdPhoto;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
