package com.tbcpl.workforce.operation.profile.dto.request;

import com.tbcpl.workforce.operation.profile.enums.RaidingAuthority;
import lombok.Data;

@Data
public class MaterialSeizedItemRequest {
    private String brandName;
    private String company;
    private String quantity;
    private String location;
    private String raidingAuthority;
    private String raidingAuthorityOther;
    private String dateSeized;
}
