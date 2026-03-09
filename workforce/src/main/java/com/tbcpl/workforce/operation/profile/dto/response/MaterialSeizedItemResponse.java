package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.RaidingAuthority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialSeizedItemResponse {
    private Long id;
    private String brandName;
    private String company;
    private String quantity;
    private String location;
    private RaidingAuthority raidingAuthority;
    private String raidingAuthorityOther;
    private LocalDate dateSeized;
}
