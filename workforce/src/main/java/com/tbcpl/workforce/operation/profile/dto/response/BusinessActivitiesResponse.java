package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.AuthorizationStatus;
import com.tbcpl.workforce.operation.profile.enums.BusinessEntityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessActivitiesResponse {
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
