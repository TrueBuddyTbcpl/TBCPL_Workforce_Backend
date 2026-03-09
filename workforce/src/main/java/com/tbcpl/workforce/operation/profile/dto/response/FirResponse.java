package com.tbcpl.workforce.operation.profile.dto.response;

import com.tbcpl.workforce.operation.profile.enums.FirStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FirResponse {
    private Long id;
    private String firNumber;
    private String caseNumber;
    private List<String> sections;
    private LocalDate dateRegistered;
    private FirStatus status;
}
