package com.tbcpl.workforce.hr.attendance.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayRequest {

    @NotBlank(message = "Holiday name is required")
    @Size(min = 2, max = 100, message = "Holiday name must be between 2 and 100 characters")
    private String holidayName;

    @NotNull(message = "Holiday date is required")
    private LocalDate holidayDate;

    @Size(max = 255)
    private String description;

    // null means all locations
    @Size(max = 100)
    private String location;

    private Boolean isOptional;
}