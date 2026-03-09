package com.tbcpl.workforce.operation.prereport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "prereport_online_presence")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreReportOnlinePresence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prereport_id", nullable = false)
    private Long prereportId;

    @Column(name = "platform_name")
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
