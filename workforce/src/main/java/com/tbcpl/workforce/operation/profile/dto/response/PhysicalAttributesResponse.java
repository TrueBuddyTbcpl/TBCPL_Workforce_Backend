package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalAttributesResponse {
    private String height;
    private String weight;
    private String eyeColor;
    private String hairColor;
    private String skinTone;
    private String identificationMarks;
    private String disabilities;
}
