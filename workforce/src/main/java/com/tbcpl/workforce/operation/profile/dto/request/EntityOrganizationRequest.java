package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EntityOrganizationRequest {

    @Valid
    private List<AssociatedCompanyRequest> associatedCompanies = new ArrayList<>();
}
