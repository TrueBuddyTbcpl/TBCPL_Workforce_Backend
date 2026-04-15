package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class VehicleRequest {
    private String make;
    private String model;
    private String registrationNumber;

    // Already String — add companion field
    private String ownershipType;
    private String ownershipTypeOther;
}