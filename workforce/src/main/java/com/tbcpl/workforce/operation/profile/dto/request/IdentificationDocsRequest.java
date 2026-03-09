package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class IdentificationDocsRequest {
    private String employeeId;
    private String aadhaarNumber;
    private String aadhaarPhoto;
    private String panNumber;
    private String panPhoto;
    private String drivingLicense;
    private String dlPhoto;
    private String passportNumber;
    private String passportPhoto;
    private String otherIdType;
    private String otherIdNumber;
    private String otherIdPhoto;
}
