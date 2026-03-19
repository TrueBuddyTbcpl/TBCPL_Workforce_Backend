package com.tbcpl.workforce.grnd_operation.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LoaRequestDto {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Client ID is required")
    private Long clientId;

    @NotNull(message = "Valid upto date is required")
    @Future(message = "Valid upto date must be a future date")
    private LocalDate validUpto;
}
