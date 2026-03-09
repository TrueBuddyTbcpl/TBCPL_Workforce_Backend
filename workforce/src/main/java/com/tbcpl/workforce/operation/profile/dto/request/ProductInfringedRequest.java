package com.tbcpl.workforce.operation.profile.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductInfringedRequest {

    @NotBlank(message = "Brand name is required")
    private String brandName;

    @NotBlank(message = "Company name is required")
    private String companyName;

    private String productType;
}
