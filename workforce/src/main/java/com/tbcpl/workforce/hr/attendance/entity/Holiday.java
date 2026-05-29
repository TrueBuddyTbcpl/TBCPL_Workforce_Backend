package com.tbcpl.workforce.hr.attendance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_holidays",
        indexes = {
                @Index(name = "idx_holiday_date",      columnList = "holiday_date"),
                @Index(name = "idx_holiday_year",      columnList = "holiday_year"),
                @Index(name = "idx_holiday_is_active", columnList = "is_active")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name  = "uq_holiday_date_location",
                        columnNames = {"holiday_date", "location"}
                )
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "holiday_name", nullable = false, length = 100)
    private String holidayName;

    @Column(name = "holiday_date", nullable = false)
    private LocalDate holidayDate;

    @Column(name = "holiday_year", nullable = false)
    private Integer holidayYear;

    @Column(name = "description", length = 255)
    private String description;

    // null = applies to all locations
    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "is_optional", nullable = false)
    @Builder.Default
    private Boolean isOptional = false;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;
}