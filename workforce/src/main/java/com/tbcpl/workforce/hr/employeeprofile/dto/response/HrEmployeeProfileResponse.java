package com.tbcpl.workforce.hr.employeeprofile.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.BloodGroup;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.EmploymentType;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.Gender;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.MaritalStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HrEmployeeProfileResponse {

    private Long id;
    private String empId;

    // Personal
    private LocalDate dateOfBirth;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private BloodGroup bloodGroup;
    private String nationality;
    private String religion;

    // Contact
    private String personalEmail;
    private String phoneNumber;
    private String alternatePhoneNumber;

    // Current Address
    private String currentAddressLine1;
    private String currentAddressLine2;
    private String currentCity;
    private String currentState;
    private String currentPincode;

    // Permanent Address
    private String permanentAddressLine1;
    private String permanentAddressLine2;
    private String permanentCity;
    private String permanentState;
    private String permanentPincode;

    // Identity (masked in list responses)
    private String panNumber;
    private String aadharNumber;
    private String passportNumber;
    private LocalDate passportExpiryDate;

    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactRelation;
    private String emergencyContactPhone;

    // Bank Details (masked in list responses)
    private String bankName;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankBranch;

    // Employment
    private EmploymentType employmentType;
    private LocalDate dateOfJoining;
    private LocalDate probationEndDate;
    private LocalDate confirmationDate;
    private LocalDate dateOfLeaving;
    private Integer noticePeriodDays;

    // Qualification
    private String highestQualification;
    private String specialization;
    private String institutionName;
    private Integer yearOfPassing;

    // Audit
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}