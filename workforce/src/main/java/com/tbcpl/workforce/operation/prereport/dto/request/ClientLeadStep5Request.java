package com.tbcpl.workforce.operation.prereport.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientLeadStep5Request {

    private String obsIdentifiableTarget;
    private String obsTraceability;
    private String obsProductVisibility;
    private String obsCounterfeitingIndications;
    private String obsEvidentiary_gaps;
    private List<CustomObservationEntry> observationsCustomData;
}
