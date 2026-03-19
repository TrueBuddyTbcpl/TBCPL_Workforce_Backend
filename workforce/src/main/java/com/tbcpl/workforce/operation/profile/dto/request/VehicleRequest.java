package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.VehicleOwnershipType;
import lombok.Data;

@Data
public class VehicleRequest {
    private String make;
    private String model;
    private String registrationNumber;
    private String ownershipType;
}
