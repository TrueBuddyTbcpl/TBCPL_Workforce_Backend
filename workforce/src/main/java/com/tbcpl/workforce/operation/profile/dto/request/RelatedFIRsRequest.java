package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RelatedFIRsRequest {

    @Valid
    private List<FirRequest> firs = new ArrayList<>();
}
