package com.tbcpl.workforce.operation.customoption.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "op_custom_dropdown_options",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_field_value",
                columnNames = {"field_name", "value"}
        ),
        indexes = @Index(name = "idx_custom_option_field", columnList = "field_name")
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpCustomDropdownOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;       // e.g. "retailerStatus", "firStatus"

    @Column(name = "value", nullable = false, length = 200)
    private String value;           // the custom text user typed

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}