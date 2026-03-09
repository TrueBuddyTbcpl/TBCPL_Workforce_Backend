package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.AssociateRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssociateRequest {

    @NotBlank(message = "Associate name is required")
    private String name;

    private String relationship;
    private AssociateRole role;
    private String contactInfo;
    private String notes;
}
