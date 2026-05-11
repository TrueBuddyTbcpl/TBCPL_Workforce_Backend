package com.tbcpl.workforce.ttr.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ttr_completion_records", indexes = {
        @Index(name = "idx_completion_ttr_id",      columnList = "ttr_id"),
        @Index(name = "idx_completion_emp_id",      columnList = "completed_by_emp_id"),
        @Index(name = "idx_completion_completed_at", columnList = "completed_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtrCompletionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ttr_id", nullable = false)
    private Ttr ttr;

    // Which cycle this is (1st completion, 2nd completion, etc.)
    @Column(name = "cycle_number", nullable = false)
    private Integer cycleNumber;

    @Column(name = "completed_by_emp_id", nullable = false, length = 20)
    private String completedByEmpId;

    @Column(name = "completed_by_name", length = 150)
    private String completedByName;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    // S3 proof file
    @Column(name = "proof_file_url", length = 512)
    private String proofFileUrl;

    @Column(name = "proof_file_name", length = 255)
    private String proofFileName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}