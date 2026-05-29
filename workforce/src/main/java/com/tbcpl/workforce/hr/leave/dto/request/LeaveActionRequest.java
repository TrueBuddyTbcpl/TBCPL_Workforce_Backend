package com.tbcpl.workforce.hr.leave.dto.request;

import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveActionRequest {

    @NotNull(message = "Action (status) is required")
    private LeaveStatus action; // APPROVED or REJECTED or REVOKED

    @lombok.NonNull
    private String remarks;
}