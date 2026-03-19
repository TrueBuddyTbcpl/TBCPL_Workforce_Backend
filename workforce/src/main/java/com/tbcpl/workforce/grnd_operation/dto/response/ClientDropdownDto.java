package com.tbcpl.workforce.grnd_operation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDropdownDto {
    private Long   clientId;
    private String clientName;
}
