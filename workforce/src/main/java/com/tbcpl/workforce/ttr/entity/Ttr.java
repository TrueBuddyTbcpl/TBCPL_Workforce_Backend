package com.tbcpl.workforce.ttr.entity;

import com.tbcpl.workforce.ttr.entity.enums.TtrModuleType;
import com.tbcpl.workforce.ttr.entity.enums.TtrStatus;
import com.tbcpl.workforce.ttr.entity.enums.TtrType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ttr", indexes = {
        @Index(name = "idx_ttr_number",      columnList = "ttr_number"),
        @Index(name = "idx_ttr_status",      columnList = "status"),
        @Index(name = "idx_ttr_department",  columnList = "department_id"),
        @Index(name = "idx_ttr_assigned",    columnList = "assigned_emp_id"),
        @Index(name = "idx_ttr_parent",      columnList = "parent_ttr_id"),
        @Index(name = "idx_ttr_module",      columnList = "module_type, linked_item_id"),
        @Index(name = "idx_ttr_created_by",  columnList = "created_by")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ttr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ttr_number", nullable = false, unique = true, length = 20)
    private String ttrNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "ttr_type", nullable = false, length = 20)
    @Builder.Default
    private TtrType ttrType = TtrType.CUSTOM;

    // ── Department (soft reference — matches existing pattern) ────────────────
    @Column(name = "department_id", nullable = false)
    private Long departmentId;

    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    // ── Assigned Employee (soft reference) ────────────────────────────────────
    @Column(name = "assigned_emp_id", nullable = false, length = 20)
    private String assignedEmpId;

    @Column(name = "assigned_emp_name", nullable = false, length = 150)
    private String assignedEmpName;

    // ── Module Integration (polymorphic soft reference) ───────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "module_type", nullable = false, length = 30)
    private TtrModuleType moduleType;

    @Column(name = "linked_item_id", nullable = false)
    private Long linkedItemId;

    @Column(name = "linked_item_display", nullable = false, length = 255)
    private String linkedItemDisplay;   // e.g. "RPT-2026-001 | ClientName"

    // ── Content ───────────────────────────────────────────────────────────────
    @Lob
    @Column(name = "notes", nullable = false, columnDefinition = "TEXT")
    private String notes;

    // ── Status ────────────────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private TtrStatus status = TtrStatus.S1_OPENED;

    // ── Hierarchy ─────────────────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_ttr_id")
    private Ttr parentTtr;

    /**
     * 0 = Root/Parent TTR (created by Admin/Super Admin)
     * 1 = Level-1 Child (created by assigned employee of Parent)
     * 2 = Level-2 Child (max depth — cannot have further children)
     */
    @Column(name = "nesting_depth", nullable = false)
    @Builder.Default
    private Integer nestingDepth = 0;

    @OneToMany(mappedBy = "parentTtr", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ttr> children = new ArrayList<>();

    @OneToMany(mappedBy = "ttr", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("completedAt DESC")
    @Builder.Default
    private List<TtrCompletionRecord> completionRecords = new ArrayList<>();

    @Column(name = "proof_attachment_url", length = 512)
    private String proofAttachmentUrl;

    @Column(name = "proof_attachment_key", length = 512)
    private String proofAttachmentKey;

    @Column(name = "proof_attachment_name", length = 255)
    private String proofAttachmentName;

    @Column(name = "proof_attachment_content_type", length = 150)
    private String proofAttachmentContentType;

    @Column(name = "proof_attachment_size")
    private Long proofAttachmentSize;

    // ── Audit ─────────────────────────────────────────────────────────────────
    @Column(name = "created_by", nullable = false, length = 20)
    private String createdBy;           // empId of creator

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Helpers ───────────────────────────────────────────────────────────────

    public boolean isParent() {
        return parentTtr == null;
    }

    public boolean canHaveChildren() {
        return nestingDepth < 2;
    }

    public boolean isClosed() {
        return status == TtrStatus.S5_CLOSED;
    }

    public boolean hasOpenChildren() {
        return children != null && children.stream()
                .anyMatch(child -> child.getStatus() != TtrStatus.S5_CLOSED);
    }
}