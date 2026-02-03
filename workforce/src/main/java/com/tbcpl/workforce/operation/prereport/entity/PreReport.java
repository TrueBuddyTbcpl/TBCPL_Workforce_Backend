package com.tbcpl.workforce.operation.prereport.entity;

import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.entity.enums.ReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prereport")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_id", unique = true, nullable = false, length = 20)
    private String reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, foreignKey = @ForeignKey(name = "fk_prereport_client"))
    private Client client;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "product_ids", columnDefinition = "json")
    private List<Long> productIds;

    @Enumerated(EnumType.STRING)
    @Column(name = "lead_type", nullable = false)
    private LeadType leadType;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false, length = 30)
    @Builder.Default
    private ReportStatus reportStatus = ReportStatus.DRAFT;

    @Column(name = "current_step")
    @Builder.Default
    private Integer currentStep = 0;

    @Lob
    @Column(name = "change_comments", columnDefinition = "TEXT")
    private String changeComments;

    @Lob
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper method to check if report can be edited
    public boolean canEdit() {
        return reportStatus == ReportStatus.DRAFT
                || reportStatus == ReportStatus.IN_PROGRESS
                || reportStatus == ReportStatus.REQUESTED_FOR_CHANGES;
    }
}
