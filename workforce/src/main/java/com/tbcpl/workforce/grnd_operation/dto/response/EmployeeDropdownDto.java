package com.tbcpl.workforce.grnd_operation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDropdownDto {
    private Long   id;
    private String empId;
    private String fullName;
    private String email;
}
