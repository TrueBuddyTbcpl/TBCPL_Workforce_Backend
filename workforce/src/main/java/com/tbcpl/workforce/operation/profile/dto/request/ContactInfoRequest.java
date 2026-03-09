package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class ContactInfoRequest {
    private String primaryPhone;
    private String secondaryPhone;
    private String primaryEmail;
    private String secondaryEmail;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;
}
