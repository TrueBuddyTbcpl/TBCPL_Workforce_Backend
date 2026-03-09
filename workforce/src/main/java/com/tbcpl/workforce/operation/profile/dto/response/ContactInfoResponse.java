package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactInfoResponse {
    private String primaryPhone;
    private String secondaryPhone;
    private String primaryEmail;
    private String secondaryEmail;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
}
