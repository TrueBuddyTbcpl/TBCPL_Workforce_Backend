package com.tbcpl.workforce.hr.employeeprofile.entity;

import com.tbcpl.workforce.hr.employeeprofile.entity.enums.BloodGroup;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.EmploymentType;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.Gender;
import com.tbcpl.workforce.hr.employeeprofile.entity.enums.MaritalStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "hr_employee_profiles",
        indexes = {
                @Index(name = "idx_hr_profile_emp_id",    columnList = "emp_id",    unique = true),
                @Index(name = "idx_hr_profile_pan",        columnList = "pan_number"),
                @Index(name = "idx_hr_profile_aadhar",     columnList = "aadhar_number"),
                @Index(name = "idx_hr_profile_is_active",  columnList = "is_active")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HrEmployeeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Core reference — no JPA cross-dept join ───────────────────────────
    @Column(name = "emp_id", nullable = false, unique = true, length = 20)
    private String empId;

    // ── Personal Details ──────────────────────────────────────────────────
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", length = 20)
    private MaritalStatus maritalStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group", length = 15)
    private BloodGroup bloodGroup;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "religion", length = 50)
    private String religion;

    // ── Contact Details ───────────────────────────────────────────────────
    @Column(name = "personal_email", length = 100)
    private String personalEmail;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(name = "alternate_phone_number", length = 15)
    private String alternatePhoneNumber;

    // ── Current Address ───────────────────────────────────────────────────
    @Column(name = "current_address_line1", length = 255)
    private String currentAddressLine1;

    @Column(name = "current_address_line2", length = 255)
    private String currentAddressLine2;

    @Column(name = "current_city", length = 100)
    private String currentCity;

    @Column(name = "current_state", length = 100)
    private String currentState;

    @Column(name = "current_pincode", length = 10)
    private String currentPincode;

    // ── Permanent Address ─────────────────────────────────────────────────
    @Column(name = "permanent_address_line1", length = 255)
    private String permanentAddressLine1;

    @Column(name = "permanent_address_line2", length = 255)
    private String permanentAddressLine2;

    @Column(name = "permanent_city", length = 100)
    private String permanentCity;

    @Column(name = "permanent_state", length = 100)
    private String permanentState;

    @Column(name = "permanent_pincode", length = 10)
    private String permanentPincode;

    // ── Identity Documents ────────────────────────────────────────────────
    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "aadhar_number", length = 20)
    private String aadharNumber;

    @Column(name = "passport_number", length = 20)
    private String passportNumber;

    @Column(name = "passport_expiry_date")
    private LocalDate passportExpiryDate;

    // ── Emergency Contact ─────────────────────────────────────────────────
    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation;

    @Column(name = "emergency_contact_phone", length = 15)
    private String emergencyContactPhone;

    // ── Bank Details ──────────────────────────────────────────────────────
    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account_number", length = 30)
    private String bankAccountNumber;

    @Column(name = "bank_ifsc_code", length = 20)
    private String bankIfscCode;

    @Column(name = "bank_branch", length = 100)
    private String bankBranch;

    // ── Employment Details ────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", length = 20)
    private EmploymentType employmentType;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;

    @Column(name = "confirmation_date")
    private LocalDate confirmationDate;

    @Column(name = "date_of_leaving")
    private LocalDate dateOfLeaving;

    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;

    // ── Qualification ─────────────────────────────────────────────────────
    @Column(name = "highest_qualification", length = 100)
    private String highestQualification;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "institution_name", length = 150)
    private String institutionName;

    @Column(name = "year_of_passing")
    private Integer yearOfPassing;

    // ── Audit ─────────────────────────────────────────────────────────────
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;
}