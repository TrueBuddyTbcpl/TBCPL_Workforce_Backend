package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class AssociatedCompanyRequest {
    private String companyName;

    // Already String — add companion field
    private String relationshipNature;
    private String relationshipNatureOther;

    private String details;
}