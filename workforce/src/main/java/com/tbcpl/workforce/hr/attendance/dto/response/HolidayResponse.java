package com.tbcpl.workforce.hr.attendance.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HolidayResponse {

    private Long          id;
    private String        holidayName;
    private LocalDate     holidayDate;
    private Integer       holidayYear;
    private String        description;
    private String        location;
    private Boolean       isOptional;
    private Boolean       isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        createdBy;
}