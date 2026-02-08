package com.tbcpl.workforce.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for password change response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChangeResponse {

    private String message;
    private LocalDate lastPasswordChangeDate;
    private LocalDate nextPasswordChangeDate;
    private Long daysUntilExpiry;
}
