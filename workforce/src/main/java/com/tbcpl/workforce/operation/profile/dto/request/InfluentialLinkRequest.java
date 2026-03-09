package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InfluentialLinkRequest {

    @NotBlank(message = "Person name is required")
    private String personName;

    private String profileDetails;
    private String relationship;
}
