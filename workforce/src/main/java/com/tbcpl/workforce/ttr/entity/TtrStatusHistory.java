package com.tbcpl.workforce.ttr.entity;

import com.tbcpl.workforce.ttr.entity.enums.TtrStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ttr_status_history", indexes = {
        @Index(name = "idx_ttr_history_ttr_id", columnList = "ttr_id, changed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtrStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ttr_id", nullable = false)
    private Ttr ttr;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 30)
    private TtrStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 30)
    private TtrStatus newStatus;

    @Column(name = "changed_by", nullable = false, length = 20)
    private String changedBy;           // empId

    @Column(name = "changed_by_name", length = 150)
    private String changedByName;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "proof_file_url", length = 512)
    private String proofFileUrl;

    @Column(name = "proof_file_name", length = 255)
    private String proofFileName;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;
}