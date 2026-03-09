package com.tbcpl.workforce.operation.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductsOperationsResponse {
    private List<ProductInfringedResponse> productsInfringed;
    private String knownModusOperandi;
    private List<String> knownLocations;
}
