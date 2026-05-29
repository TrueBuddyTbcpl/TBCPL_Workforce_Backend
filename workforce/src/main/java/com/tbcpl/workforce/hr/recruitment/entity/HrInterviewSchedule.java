package com.tbcpl.workforce.hr.recruitment.entity;

import com.tbcpl.workforce.hr.recruitment.entity.enums.InterviewRound;
import com.tbcpl.workforce.hr.recruitment.entity.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_interview_schedules",
        indexes = {
                @Index(name = "idx_interview_candidate", columnList = "candidate_id"),
                @Index(name = "idx_interview_round",     columnList = "round"),
                @Index(name = "idx_interview_status",    columnList = "status"),
                @Index(name = "idx_interview_scheduled_at", columnList = "scheduled_at"),
                @Index(name = "idx_interview_is_active", columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrInterviewSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private HrCandidate candidate;

    @Enumerated(EnumType.STRING)
    @Column(name = "round", nullable = false, length = 30)
    private InterviewRound round;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    // Interviewer — String ref, no cross-dept join
    @Column(name = "interviewer_emp_id", length = 20)
    private String interviewerEmpId;

    @Column(name = "interviewer_name", length = 100)
    private String interviewerName;

    // Online/Offline/Phone
    @Column(name = "mode", length = 30)
    private String mode;

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;

    @Column(name = "venue", length = 255)
    private String venue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private InterviewStatus status = InterviewStatus.SCHEDULED;

    // Feedback filled after interview completion
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    // Score out of 10
    @Column(name = "score")
    private Double score;

    // SELECTED / REJECTED / ON_HOLD
    @Column(name = "result", length = 30)
    private String result;

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