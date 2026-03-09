package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductsOperationsRequest {

    @Valid
    private List<ProductInfringedRequest> productsInfringed = new ArrayList<>();

    private String knownModusOperandi;
    private List<String> knownLocations = new ArrayList<>();
}
