package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.AuthorizationStatus;
import com.tbcpl.workforce.operation.profile.enums.BusinessEntityStatus;
import lombok.Data;

@Data
public class BusinessActivitiesRequest {
    private BusinessEntityStatus retailerStatus;
    private AuthorizationStatus retailerType;
    private String retailerDetails;
    private BusinessEntityStatus supplierStatus;
    private AuthorizationStatus supplierType;
    private String supplierDetails;
    private BusinessEntityStatus manufacturerStatus;
    private AuthorizationStatus manufacturerType;
    private String manufacturerDetails;
}
