package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentificationDocsResponse {
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
