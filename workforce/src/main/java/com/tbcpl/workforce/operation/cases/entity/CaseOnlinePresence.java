package com.tbcpl.workforce.operation.cases.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "op_case_online_presence", indexes = {
        @Index(name = "idx_case_online_presence_case_id", columnList = "case_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseOnlinePresence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_case_online_presence_case"))
    private Case caseEntity;

    @Column(name = "platform_name", length = 100)
    private String platformName;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
