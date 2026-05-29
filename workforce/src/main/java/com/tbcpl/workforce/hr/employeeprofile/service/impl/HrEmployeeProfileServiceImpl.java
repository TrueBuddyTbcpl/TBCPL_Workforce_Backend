package com.tbcpl.workforce.hr.employeeprofile.service.impl;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.employeeprofile.dto.request.HrEmployeeProfileRequest;
import com.tbcpl.workforce.hr.employeeprofile.dto.response.HrEmployeeListResponse;
import com.tbcpl.workforce.hr.employeeprofile.dto.response.HrEmployeeProfileResponse;
import com.tbcpl.workforce.hr.employeeprofile.entity.HrEmployeeProfile;
import com.tbcpl.workforce.hr.employeeprofile.repository.HrEmployeeProfileRepository;
import com.tbcpl.workforce.hr.employeeprofile.service.HrEmployeeProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrEmployeeProfileServiceImpl implements HrEmployeeProfileService {

    private final HrEmployeeProfileRepository profileRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeNameResolverService nameResolver;

    @Override
    @Transactional
    public HrEmployeeProfileResponse createProfile(HrEmployeeProfileRequest request, String createdBy) {
        log.info("Creating HR profile for empId: {}", request.getEmpId());

        validateEmployeeExists(request.getEmpId());

        if (profileRepository.existsByEmpId(request.getEmpId())) {
            throw new DuplicateResourceException(
                    "HR profile already exists for empId: " + request.getEmpId());
        }

        validateUniqueIdentityDocuments(request, null);

        HrEmployeeProfile profile = buildEntity(request, createdBy);
        HrEmployeeProfile saved = profileRepository.save(profile);

        log.info("HR profile created with ID: {} for empId: {}", saved.getId(), saved.getEmpId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrEmployeeProfileResponse getProfileById(Long id) {
        log.info("Fetching HR profile by ID: {}", id);
        HrEmployeeProfile profile = findById(id);
        return mapToResponse(profile, resolveCreatedBy(profile.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrEmployeeProfileResponse getProfileByEmpId(String empId) {
        log.info("Fetching HR profile by empId: {}", empId);
        HrEmployeeProfile profile = profileRepository.findByEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "HR profile not found for empId: " + empId));
        return mapToResponse(profile, resolveCreatedBy(profile.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrEmployeeProfileResponse> getAllProfiles(int page, int size, String empId) {
        log.info("Fetching all HR profiles - page:{} size:{} empId:{}", page, size, empId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<HrEmployeeProfile> profiles =
                (empId != null && !empId.isBlank())
                        ? profileRepository.searchByEmpId(empId.trim(), pageable)
                        : profileRepository.findByIsActiveTrue(pageable);

        Set<String> createdBySet = new HashSet<>();
        profiles.forEach(p -> {
            if (p.getCreatedBy() != null && !p.getCreatedBy().isBlank()) {
                createdBySet.add(p.getCreatedBy());
            }
        });

        Map<String, String> nameMap = nameResolver.resolve(createdBySet);
        return profiles.map(p -> mapToResponse(p, nameMap));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrEmployeeListResponse> getAllEmployees(int page, int size, String search) {
        log.info("Fetching HR employee list - page:{} size:{} search:{}", page, size, search);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Employee> employeePage = fetchEmployees(search, pageable);

        if (employeePage.isEmpty()) {
            return employeePage.map(employee -> null);
        }

        java.util.List<String> empIds = employeePage.getContent().stream()
                .map(Employee::getEmpId)
                .toList();

        Map<String, HrEmployeeProfile> profileMap = profileRepository
                .findByEmpIdInAndIsActiveTrue(empIds)
                .stream()
                .collect(Collectors.toMap(HrEmployeeProfile::getEmpId, profile -> profile));

        return employeePage.map(employee -> mapToEmployeeListResponse(employee, profileMap.get(employee.getEmpId())));
    }

    @Override
    @Transactional
    public HrEmployeeProfileResponse updateProfile(Long id, HrEmployeeProfileRequest request, String updatedBy) {
        log.info("Updating HR profile ID {} by {}", id, updatedBy);

        HrEmployeeProfile profile = findById(id);
        validateUniqueIdentityDocuments(request, id);
        updateEntityFields(profile, request);

        HrEmployeeProfile saved = profileRepository.save(profile);

        log.info("HR profile ID {} updated successfully", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteProfile(Long id, String deletedBy) {
        log.info("Soft deleting HR profile ID: {} by: {}", id, deletedBy);
        HrEmployeeProfile profile = findById(id);
        profile.setIsActive(false);
        profileRepository.save(profile);
        log.info("HR profile ID: {} soft deleted", id);
    }

    private Page<Employee> fetchEmployees(String search, Pageable pageable) {
        if (search == null || search.isBlank()) {
            return employeeRepository.findByIsActiveTrue(pageable);
        }
        return employeeRepository.findByName("%" + search.trim().toLowerCase() + "%", pageable);
    }

    private HrEmployeeProfile findById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "HR employee profile not found with ID: " + id));
    }

    private void validateEmployeeExists(String empId) {
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId + ". Please create employee account first.");
        }
    }

    private void validateUniqueIdentityDocuments(HrEmployeeProfileRequest request, Long excludeId) {
        if (request.getPanNumber() != null && !request.getPanNumber().isBlank()) {
            boolean panExists = excludeId == null
                    ? profileRepository.existsByPanNumber(request.getPanNumber())
                    : profileRepository.existsByPanNumberAndIdNot(request.getPanNumber(), excludeId);

            if (panExists) {
                throw new DuplicateResourceException(
                        "PAN number already registered: " + request.getPanNumber());
            }
        }

        if (request.getAadharNumber() != null && !request.getAadharNumber().isBlank()) {
            boolean aadharExists = excludeId == null
                    ? profileRepository.existsByAadharNumber(request.getAadharNumber())
                    : profileRepository.existsByAadharNumberAndIdNot(request.getAadharNumber(), excludeId);

            if (aadharExists) {
                throw new DuplicateResourceException(
                        "Aadhar number already registered: " + request.getAadharNumber());
            }
        }
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) {
            return Collections.emptyMap();
        }
        return nameResolver.resolve(Set.of(createdBy));
    }

    private HrEmployeeProfile buildEntity(HrEmployeeProfileRequest req, String createdBy) {
        return HrEmployeeProfile.builder()
                .empId(req.getEmpId().trim())
                .dateOfBirth(req.getDateOfBirth())
                .gender(req.getGender())
                .maritalStatus(req.getMaritalStatus())
                .bloodGroup(req.getBloodGroup())
                .nationality(req.getNationality())
                .religion(req.getReligion())
                .personalEmail(req.getPersonalEmail())
                .phoneNumber(req.getPhoneNumber())
                .alternatePhoneNumber(req.getAlternatePhoneNumber())
                .currentAddressLine1(req.getCurrentAddressLine1())
                .currentAddressLine2(req.getCurrentAddressLine2())
                .currentCity(req.getCurrentCity())
                .currentState(req.getCurrentState())
                .currentPincode(req.getCurrentPincode())
                .permanentAddressLine1(req.getPermanentAddressLine1())
                .permanentAddressLine2(req.getPermanentAddressLine2())
                .permanentCity(req.getPermanentCity())
                .permanentState(req.getPermanentState())
                .permanentPincode(req.getPermanentPincode())
                .panNumber(req.getPanNumber())
                .aadharNumber(req.getAadharNumber())
                .passportNumber(req.getPassportNumber())
                .passportExpiryDate(req.getPassportExpiryDate())
                .emergencyContactName(req.getEmergencyContactName())
                .emergencyContactRelation(req.getEmergencyContactRelation())
                .emergencyContactPhone(req.getEmergencyContactPhone())
                .bankName(req.getBankName())
                .bankAccountNumber(req.getBankAccountNumber())
                .bankIfscCode(req.getBankIfscCode())
                .bankBranch(req.getBankBranch())
                .employmentType(req.getEmploymentType())
                .dateOfJoining(req.getDateOfJoining())
                .probationEndDate(req.getProbationEndDate())
                .confirmationDate(req.getConfirmationDate())
                .dateOfLeaving(req.getDateOfLeaving())
                .noticePeriodDays(req.getNoticePeriodDays())
                .highestQualification(req.getHighestQualification())
                .specialization(req.getSpecialization())
                .institutionName(req.getInstitutionName())
                .yearOfPassing(req.getYearOfPassing())
                .isActive(true)
                .createdBy(createdBy)
                .build();
    }

    private void updateEntityFields(HrEmployeeProfile profile, HrEmployeeProfileRequest req) {
        if (req.getDateOfBirth() != null) profile.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null) profile.setGender(req.getGender());
        if (req.getMaritalStatus() != null) profile.setMaritalStatus(req.getMaritalStatus());
        if (req.getBloodGroup() != null) profile.setBloodGroup(req.getBloodGroup());
        if (req.getNationality() != null) profile.setNationality(req.getNationality());
        if (req.getReligion() != null) profile.setReligion(req.getReligion());
        if (req.getPersonalEmail() != null) profile.setPersonalEmail(req.getPersonalEmail());
        if (req.getPhoneNumber() != null) profile.setPhoneNumber(req.getPhoneNumber());
        if (req.getAlternatePhoneNumber() != null) profile.setAlternatePhoneNumber(req.getAlternatePhoneNumber());
        if (req.getCurrentAddressLine1() != null) profile.setCurrentAddressLine1(req.getCurrentAddressLine1());
        if (req.getCurrentAddressLine2() != null) profile.setCurrentAddressLine2(req.getCurrentAddressLine2());
        if (req.getCurrentCity() != null) profile.setCurrentCity(req.getCurrentCity());
        if (req.getCurrentState() != null) profile.setCurrentState(req.getCurrentState());
        if (req.getCurrentPincode() != null) profile.setCurrentPincode(req.getCurrentPincode());
        if (req.getPermanentAddressLine1() != null) profile.setPermanentAddressLine1(req.getPermanentAddressLine1());
        if (req.getPermanentAddressLine2() != null) profile.setPermanentAddressLine2(req.getPermanentAddressLine2());
        if (req.getPermanentCity() != null) profile.setPermanentCity(req.getPermanentCity());
        if (req.getPermanentState() != null) profile.setPermanentState(req.getPermanentState());
        if (req.getPermanentPincode() != null) profile.setPermanentPincode(req.getPermanentPincode());
        if (req.getPanNumber() != null) profile.setPanNumber(req.getPanNumber());
        if (req.getAadharNumber() != null) profile.setAadharNumber(req.getAadharNumber());
        if (req.getPassportNumber() != null) profile.setPassportNumber(req.getPassportNumber());
        if (req.getPassportExpiryDate() != null) profile.setPassportExpiryDate(req.getPassportExpiryDate());
        if (req.getEmergencyContactName() != null) profile.setEmergencyContactName(req.getEmergencyContactName());
        if (req.getEmergencyContactRelation() != null) profile.setEmergencyContactRelation(req.getEmergencyContactRelation());
        if (req.getEmergencyContactPhone() != null) profile.setEmergencyContactPhone(req.getEmergencyContactPhone());
        if (req.getBankName() != null) profile.setBankName(req.getBankName());
        if (req.getBankAccountNumber() != null) profile.setBankAccountNumber(req.getBankAccountNumber());
        if (req.getBankIfscCode() != null) profile.setBankIfscCode(req.getBankIfscCode());
        if (req.getBankBranch() != null) profile.setBankBranch(req.getBankBranch());
        if (req.getEmploymentType() != null) profile.setEmploymentType(req.getEmploymentType());
        if (req.getDateOfJoining() != null) profile.setDateOfJoining(req.getDateOfJoining());
        if (req.getProbationEndDate() != null) profile.setProbationEndDate(req.getProbationEndDate());
        if (req.getConfirmationDate() != null) profile.setConfirmationDate(req.getConfirmationDate());
        if (req.getDateOfLeaving() != null) profile.setDateOfLeaving(req.getDateOfLeaving());
        if (req.getNoticePeriodDays() != null) profile.setNoticePeriodDays(req.getNoticePeriodDays());
        if (req.getHighestQualification() != null) profile.setHighestQualification(req.getHighestQualification());
        if (req.getSpecialization() != null) profile.setSpecialization(req.getSpecialization());
        if (req.getInstitutionName() != null) profile.setInstitutionName(req.getInstitutionName());
        if (req.getYearOfPassing() != null) profile.setYearOfPassing(req.getYearOfPassing());
    }

    private HrEmployeeProfileResponse mapToResponse(HrEmployeeProfile p, Map<String, String> nameMap) {
        String raw = p.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HrEmployeeProfileResponse.builder()
                .id(p.getId())
                .empId(p.getEmpId())
                .dateOfBirth(p.getDateOfBirth())
                .gender(p.getGender())
                .maritalStatus(p.getMaritalStatus())
                .bloodGroup(p.getBloodGroup())
                .nationality(p.getNationality())
                .religion(p.getReligion())
                .personalEmail(p.getPersonalEmail())
                .phoneNumber(p.getPhoneNumber())
                .alternatePhoneNumber(p.getAlternatePhoneNumber())
                .currentAddressLine1(p.getCurrentAddressLine1())
                .currentAddressLine2(p.getCurrentAddressLine2())
                .currentCity(p.getCurrentCity())
                .currentState(p.getCurrentState())
                .currentPincode(p.getCurrentPincode())
                .permanentAddressLine1(p.getPermanentAddressLine1())
                .permanentAddressLine2(p.getPermanentAddressLine2())
                .permanentCity(p.getPermanentCity())
                .permanentState(p.getPermanentState())
                .permanentPincode(p.getPermanentPincode())
                .panNumber(p.getPanNumber())
                .aadharNumber(p.getAadharNumber())
                .passportNumber(p.getPassportNumber())
                .passportExpiryDate(p.getPassportExpiryDate())
                .emergencyContactName(p.getEmergencyContactName())
                .emergencyContactRelation(p.getEmergencyContactRelation())
                .emergencyContactPhone(p.getEmergencyContactPhone())
                .bankName(p.getBankName())
                .bankAccountNumber(p.getBankAccountNumber())
                .bankIfscCode(p.getBankIfscCode())
                .bankBranch(p.getBankBranch())
                .employmentType(p.getEmploymentType())
                .dateOfJoining(p.getDateOfJoining())
                .probationEndDate(p.getProbationEndDate())
                .confirmationDate(p.getConfirmationDate())
                .dateOfLeaving(p.getDateOfLeaving())
                .noticePeriodDays(p.getNoticePeriodDays())
                .highestQualification(p.getHighestQualification())
                .specialization(p.getSpecialization())
                .institutionName(p.getInstitutionName())
                .yearOfPassing(p.getYearOfPassing())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }

    private HrEmployeeListResponse mapToEmployeeListResponse(Employee employee, HrEmployeeProfile profile) {
        return HrEmployeeListResponse.builder()
                .empId(employee.getEmpId())
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null)
                .roleName(employee.getRole() != null ? employee.getRole().getRoleName() : null)
                .reportingManagerName(
                        employee.getReportingManager() != null
                                ? employee.getReportingManager().getFullName()
                                : null
                )
                .phoneNumber(profile != null ? profile.getPhoneNumber() : null)
                .personalEmail(profile != null ? profile.getPersonalEmail() : null)
                .employmentType(profile != null && profile.getEmploymentType() != null
                        ? profile.getEmploymentType().name()
                        : null)
                .dateOfJoining(profile != null ? profile.getDateOfJoining() : null)
                .isActive(employee.getIsActive())
                .profileCompleted(profile != null)
                .employeeCreatedAt(employee.getCreatedAt())
                .profileUpdatedAt(profile != null ? profile.getUpdatedAt() : null)
                .build();
    }
}