package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EntityOrganizationRequest {

    private List<AssociatedCompanyRequest> associatedCompanies = new ArrayList<>();
}
