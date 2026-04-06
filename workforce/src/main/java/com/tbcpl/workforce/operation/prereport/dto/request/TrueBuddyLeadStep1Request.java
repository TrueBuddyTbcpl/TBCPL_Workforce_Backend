package com.tbcpl.workforce.operation.prereport.dto.request;

import com.tbcpl.workforce.operation.prereport.entity.enums.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrueBuddyLeadStep1Request {

    private LocalDate dateInternalLeadGeneration;
    private ProductCategory productCategory;
    private String productCategoryCustomText;       // ← CHANGED: Long → String
    private InfringementType infringementType;
    private String infringementTypeCustomText;      // ← CHANGED: Long → String
    private String broadGeography;
    private java.util.List<ReasonOfSuspicion> reasonOfSuspicion;
    private String reasonOfSuspicionCustomText;     // ← CHANGED: Long → String
    private String expectedSeizure;
    private NatureOfEntity natureOfEntity;
    private String natureOfEntityCustomText;        // ← CHANGED: Long → String
}