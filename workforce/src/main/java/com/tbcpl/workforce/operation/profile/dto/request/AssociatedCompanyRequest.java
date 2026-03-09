package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.RelationshipNature;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssociatedCompanyRequest {

    @NotBlank(message = "Company name is required")
    private String companyName;

    private RelationshipNature relationshipNature;
    private String details;
}
