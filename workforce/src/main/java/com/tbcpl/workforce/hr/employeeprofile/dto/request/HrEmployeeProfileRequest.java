package com.tbcpl.workforce.hr.employeeprofile.dto.request;

import com.tbcpl.workforce.hr.employeeprofile.entity.enums.BloodGroup;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.EmploymentType;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.Gender;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.MaritalStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrEmployeeProfileRequest {

    @NotBlank(message = "Employee ID is required")
    private String empId;

    // ── Personal ──────────────────────────────────────────────────────────
    private LocalDate dateOfBirth;
    private Gender gender;
    private MaritalStatus maritalStatus;
    private BloodGroup bloodGroup;

    @Size(max = 50, message = "Nationality must not exceed 50 characters")
    private String nationality;

    @Size(max = 50, message = "Religion must not exceed 50 characters")
    private String religion;

    // ── Contact ───────────────────────────────────────────────────────────
    @Email(message = "Invalid personal email format")
    @Size(max = 100)
    private String personalEmail;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid Indian mobile number")
    private String phoneNumber;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid alternate mobile number")
    private String alternatePhoneNumber;

    // ── Current Address ───────────────────────────────────────────────────
    @Size(max = 255)
    private String currentAddressLine1;

    @Size(max = 255)
    private String currentAddressLine2;

    @Size(max = 100)
    private String currentCity;

    @Size(max = 100)
    private String currentState;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode")
    private String currentPincode;

    // ── Permanent Address ─────────────────────────────────────────────────
    @Size(max = 255)
    private String permanentAddressLine1;

    @Size(max = 255)
    private String permanentAddressLine2;

    @Size(max = 100)
    private String permanentCity;

    @Size(max = 100)
    private String permanentState;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode")
    private String permanentPincode;

    // ── Identity ──────────────────────────────────────────────────────────
    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$",
            message = "Invalid PAN format (e.g. ABCDE1234F)"
    )
    private String panNumber;

    @Pattern(
            regexp = "^[2-9]{1}[0-9]{11}$",
            message = "Invalid Aadhar number (12 digits)"
    )
    private String aadharNumber;

    @Size(max = 20)
    private String passportNumber;

    private LocalDate passportExpiryDate;

    // ── Emergency Contact ─────────────────────────────────────────────────
    @Size(max = 100)
    private String emergencyContactName;

    @Size(max = 50)
    private String emergencyContactRelation;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid emergency contact number")
    private String emergencyContactPhone;

    // ── Bank Details ──────────────────────────────────────────────────────
    @Size(max = 100)
    private String bankName;

    @Size(max = 30)
    private String bankAccountNumber;

    @Pattern(
            regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
            message = "Invalid IFSC code (e.g. SBIN0001234)"
    )
    private String bankIfscCode;

    @Size(max = 100)
    private String bankBranch;

    // ── Employment ────────────────────────────────────────────────────────
    private EmploymentType employmentType;
    private LocalDate dateOfJoining;
    private LocalDate probationEndDate;
    private LocalDate confirmationDate;
    private LocalDate dateOfLeaving;

    @Min(value = 0, message = "Notice period cannot be negative")
    @Max(value = 180, message = "Notice period cannot exceed 180 days")
    private Integer noticePeriodDays;

    // ── Qualification ─────────────────────────────────────────────────────
    @Size(max = 100)
    private String highestQualification;

    @Size(max = 100)
    private String specialization;

    @Size(max = 150)
    private String institutionName;

    @Min(value = 1950) @Max(value = 2100)
    private Integer yearOfPassing;
}