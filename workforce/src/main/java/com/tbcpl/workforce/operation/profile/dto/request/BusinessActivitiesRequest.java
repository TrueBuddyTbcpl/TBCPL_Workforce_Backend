package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class BusinessActivitiesRequest {

    // ── Was: BusinessEntityStatus / AuthorizationStatus enums ────────────────
    private String retailerStatus;
    private String retailerStatusOther;
    private String retailerType;
    private String retailerTypeOther;
    private String retailerDetails;

    private String supplierStatus;
    private String supplierStatusOther;
    private String supplierType;
    private String supplierTypeOther;
    private String supplierDetails;

    private String manufacturerStatus;
    private String manufacturerStatusOther;
    private String manufacturerType;
    private String manufacturerTypeOther;
    private String manufacturerDetails;
    // ────────────────────────────────────────────────────────────────────────
}