package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class AssociateRequest {
    private String name;
    private String relationship;

    // Already String — add companion field
    private String role;
    private String roleOther;

    private String contactInfo;
    private String notes;
}