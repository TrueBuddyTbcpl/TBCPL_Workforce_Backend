package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.RelationshipNature;
import lombok.Data;

@Data
public class AssociatedCompanyRequest {

    private String companyName;

    private String relationshipNature;
    private String details;
}
