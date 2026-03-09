package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class PhysicalAttributesRequest {
    private String height;
    private String weight;
    private String eyeColor;
    private String hairColor;
    private String skinTone;
    private String identificationMarks;
    private String disabilities;
}
