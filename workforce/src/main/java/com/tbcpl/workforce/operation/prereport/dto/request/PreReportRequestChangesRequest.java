package com.tbcpl.workforce.operation.prereport.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreReportRequestChangesRequest {

    @NotBlank(message = "Change comments are required")
    private String changeComments;
}
