package com.tbcpl.workforce.hr.recruitment.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.recruitment.entity.enums.InterviewRound;
import com.tbcpl.workforce.hr.recruitment.entity.enums.InterviewStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrInterviewScheduleResponse {

    private Long            id;
    private Long            candidateId;
    private String          candidateName;
    private String          candidateEmail;
    private InterviewRound  round;
    private LocalDateTime   scheduledAt;
    private String          interviewerEmpId;
    private String          interviewerName;
    private String          mode;
    private String          meetingLink;
    private String          venue;
    private InterviewStatus status;
    private String          feedback;
    private Double          score;
    private String          result;
    private Boolean         isActive;
    private LocalDateTime   createdAt;
    private LocalDateTime   updatedAt;
    private String          createdBy;
}