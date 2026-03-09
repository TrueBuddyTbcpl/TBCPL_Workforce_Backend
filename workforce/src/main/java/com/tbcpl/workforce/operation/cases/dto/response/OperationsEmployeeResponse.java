package com.tbcpl.workforce.operation.cases.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OperationsEmployeeResponse {
    private Long id;
    private String empId;
    private String fullName;
    private String email;
    private String roleName;
    private String departmentName;
}
