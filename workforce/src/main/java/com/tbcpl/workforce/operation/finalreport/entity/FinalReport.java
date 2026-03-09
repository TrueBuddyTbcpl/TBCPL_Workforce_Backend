package com.tbcpl.workforce.operation.finalreport.entity;

import com.tbcpl.workforce.operation.finalreport.entity.enums.FinalReportStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "operation_final_report",
        indexes = {
                @Index(name = "idx_final_report_case_id",    columnList = "case_id"),
                @Index(name = "idx_final_report_status",     columnList = "report_status"),
                @Index(name = "idx_final_report_created_by", columnList = "created_by"),
                @Index(name = "idx_final_report_is_deleted", columnList = "is_deleted")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_number", unique = true, nullable = false, length = 50)
    private String reportNumber;

    // ── Case linkage ───────────────────────────────────────────────────
    @Column(name = "case_id", unique = true, nullable = false)
    private Long caseId;

    @Column(name = "case_number", nullable = false, length = 50)
    private String caseNumber;

    // ── Client info (denormalized for report independence) ─────────────
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "client_name", nullable = false, length = 255)
    private String clientName;

    @Column(name = "client_logo_url", length = 1000)
    private String clientLogoUrl;

    // ── Report header ──────────────────────────────────────────────────
    @Column(name = "report_title", nullable = false, length = 500)
    private String reportTitle;

    @Column(name = "report_subtitle", length = 500)
    private String reportSubtitle;

    @Column(name = "prepared_for", nullable = false, length = 255)
    private String preparedFor;

    @Column(name = "prepared_by", nullable = false, length = 255)
    private String preparedBy;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    // ── Dynamic content ────────────────────────────────────────────────
    @Column(name = "sections_json", columnDefinition = "LONGTEXT")
    private String sectionsJson;

    @Column(name = "table_of_contents_json", columnDefinition = "TEXT")
    private String tableOfContentsJson;

    // ── Status lifecycle ───────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false, length = 30)
    @Builder.Default
    private FinalReportStatus reportStatus = FinalReportStatus.DRAFT;

    @Column(name = "change_comments", columnDefinition = "TEXT")
    private String changeComments;

    // ── Soft delete ────────────────────────────────────────────────────
    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    // ── Audit ──────────────────────────────────────────────────────────
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
