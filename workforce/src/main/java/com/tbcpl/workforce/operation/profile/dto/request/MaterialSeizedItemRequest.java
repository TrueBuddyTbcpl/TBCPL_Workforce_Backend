package com.tbcpl.workforce.operation.profile.dto.request;

import lombok.Data;

@Data
public class MaterialSeizedItemRequest {
    private String brandName;
    private String company;
    private String quantity;
    private String location;

    // Already String + already has other ✅ — no change needed
    private String raidingAuthority;
    private String raidingAuthorityOther;

    private String dateSeized;
}