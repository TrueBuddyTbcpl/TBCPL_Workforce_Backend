package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class AddressRequest {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private Boolean permanentSameAsCurrent;
    private String permAddressLine1;
    private String permAddressLine2;
    private String permCity;
    private String permState;
    private String permPincode;
    private String permCountry;
}
