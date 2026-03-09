package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluentialLinkResponse {
    private Long id;
    private String personName;
    private String profileDetails;
    private String relationship;
}
