package com.tbcpl.workforce.hr.recruitment.dto.request;

import com.tbcpl.workforce.hr.recruitment.entity.enums.InterviewRound;
import com.tbcpl.workforce.hr.recruitment.entity.enums.InterviewStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrInterviewScheduleRequest {

    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    @NotNull(message = "Interview round is required")
    private InterviewRound round;

    @NotNull(message = "Scheduled date and time is required")
    private LocalDateTime scheduledAt;

    @Size(max = 20)
    private String interviewerEmpId;

    @Size(max = 100)
    private String interviewerName;

    @Size(max = 30)
    private String mode; // ONLINE / OFFLINE / PHONE

    @Size(max = 500)
    private String meetingLink;

    @Size(max = 255)
    private String venue;

    // For feedback submission
    private InterviewStatus status;

    private String feedback;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0")
    private Double score;

    @Size(max = 30)
    private String result; // SELECTED / REJECTED / ON_HOLD
}