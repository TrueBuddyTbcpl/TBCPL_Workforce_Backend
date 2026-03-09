package com.tbcpl.workforce.operation.prereport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDropdownResponse {

    private Long clientId;
    private String clientName;
}
