package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GeographicExposureRequest {
    private List<String> operatingRegions = new ArrayList<>();
    private List<String> markets = new ArrayList<>();
    private List<String> jurisdictions = new ArrayList<>();
}
