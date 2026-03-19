package com.tbcpl.workforce.grnd_operation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Singleton — only ONE row ever exists (company-level assets).
 * Always access via loaAssetsRepository.findTopByOrderByIdAsc()
 */
@Entity
@Table(name = "loa_assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoaAssets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    @Column(name = "logo_public_id", length = 255)
    private String logoPublicId;

    @Column(name = "stamp_url", length = 512)
    private String stampUrl;

    @Column(name = "stamp_public_id", length = 255)
    private String stampPublicId;

    @Column(name = "signature_url", length = 512)
    private String signatureUrl;

    @Column(name = "signature_public_id", length = 255)
    private String signaturePublicId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;
}
