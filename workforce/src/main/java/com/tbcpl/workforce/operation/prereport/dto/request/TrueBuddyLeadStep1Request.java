package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.InfringementType;
import com.tbcpl.workforce.operation.prereport.entity.enums.NatureOfEntity;
import com.tbcpl.workforce.operation.prereport.entity.enums.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep1Request {

    private LocalDate dateInternalLeadGeneration;
    private ProductCategory productCategory;
    private InfringementType infringementType;
    private String broadGeography;
    private String clientSpocName;
    private String clientSpocDesignation;
    private NatureOfEntity natureOfEntity;
}
