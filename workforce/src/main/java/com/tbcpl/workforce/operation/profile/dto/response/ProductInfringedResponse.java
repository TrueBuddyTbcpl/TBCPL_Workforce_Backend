package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfringedResponse {
    private Long id;
    private String brandName;
    private String companyName;
    private String productType;
}
