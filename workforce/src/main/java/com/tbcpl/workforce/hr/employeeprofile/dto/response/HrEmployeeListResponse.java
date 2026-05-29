package com.tbcpl.workforce.hr.employeeprofile.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class HrEmployeeListResponse {

    private String empId;
    private String fullName;
    private String email;
    private String departmentName;
    private String roleName;
    private String reportingManagerName;

    private String phoneNumber;
    private String personalEmail;
    private String employmentType;
    private LocalDate dateOfJoining;

    private Boolean isActive;
    private Boolean profileCompleted;

    private LocalDateTime employeeCreatedAt;
    private LocalDateTime profileUpdatedAt;
}