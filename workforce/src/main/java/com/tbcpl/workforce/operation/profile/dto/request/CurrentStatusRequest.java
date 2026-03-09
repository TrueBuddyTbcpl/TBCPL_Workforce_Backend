package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.ProfileStatus;
import lombok.Data;

@Data
public class CurrentStatusRequest {
    private ProfileStatus status;
    private String lastKnownLocation;
    private String statusDate;
    private String remarks;
}
