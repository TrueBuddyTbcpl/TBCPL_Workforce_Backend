package com.tbcpl.workforce.grnd_operation.dto.response;

import com.tbcpl.workforce.grnd_operation.entity.enums.LoaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoaResponseDto {
    private Long          id;
    private String        loaNumber;
    private Long          employeeId;
    private String        employeeName;
    private String        employeeEmail;
    private Long          clientId;
    private String        clientName;
    private LocalDate     validUpto;
    private LoaStatus     status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String        createdBy;
}
