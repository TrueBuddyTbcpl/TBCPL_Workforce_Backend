package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.SiblingRelationship;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SiblingRequest {

    @NotBlank(message = "Sibling name is required")
    private String name;

    private SiblingRelationship relationship;
    private String occupation;
}
