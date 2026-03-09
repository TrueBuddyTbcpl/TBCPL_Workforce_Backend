package com.tbcpl.workforce.operation.cases.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CaseOnlinePresenceResponse {
    private Long id;
    private String platformName;
    private String link;
}
