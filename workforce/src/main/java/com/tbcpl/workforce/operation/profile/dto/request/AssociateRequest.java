package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.AssociateRole;
import lombok.Data;

@Data
public class AssociateRequest {

    private String name;

    private String relationship;
    private String role;
    private String contactInfo;
    private String notes;
}
