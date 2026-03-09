package com.tbcpl.workforce.auth.dto.response;

import com.tbcpl.workforce.common.enums.LoginAttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for login attempt log response
 * HR can view this for monitoring
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginAttemptResponse {

    private Long id;

    private String empId;
    private String employeeName;
    private String email;

    private LocalDateTime attemptTime;
    private String deviceIdentifier;
    private String ipAddress;
    private String userAgent;

    private LoginAttemptStatus status;
    private String statusDescription;
    private String failureReason;
}
