package com.tbcpl.workforce.operation.prereport.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDropdownResponse {

    private Long productId;
    private String productName;
    private Long clientId;
}
