package com.tbcpl.workforce.ttr.dto.request;

import com.tbcpl.workforce.ttr.entity.enums.TtrModuleType;
import com.tbcpl.workforce.ttr.entity.enums.TtrType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TtrCreateRequest {

    @NotNull(message = "TTR type is required")
    private TtrType ttrType;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotBlank(message = "Assigned employee empId is required")
    private String assignedEmpId;

    @NotNull(message = "Module type is required")
    private TtrModuleType moduleType;

    @NotNull(message = "Linked item ID is required")
    private Long linkedItemId;

    @NotBlank(message = "Notes are required")
    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}