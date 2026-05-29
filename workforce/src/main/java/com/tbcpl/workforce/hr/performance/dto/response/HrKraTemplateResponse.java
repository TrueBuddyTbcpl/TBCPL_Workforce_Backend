package com.tbcpl.workforce.hr.performance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.performance.entity.enums.KraStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrKraTemplateResponse {

    private Long          id;
    private String        kraName;
    private String        kraDescription;
    private String        designation;
    private String        department;
    private Double        weightage;
    private String        targetValue;
    private String        measurementUnit;
    private KraStatus     status;
    private Boolean       isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        createdBy;
}