package com.tbcpl.workforce.hr.employeeprofile.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.common.response.PageResponse;
import com.tbcpl.workforce.hr.employeeprofile.dto.request.HrEmployeeProfileRequest;
import com.tbcpl.workforce.hr.employeeprofile.dto.response.HrEmployeeListResponse;
import com.tbcpl.workforce.hr.employeeprofile.dto.response.HrEmployeeProfileResponse;
import com.tbcpl.workforce.hr.employeeprofile.service.HrEmployeeProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.HR_BASE)
@RequiredArgsConstructor
@Slf4j
public class HrEmployeeProfileController {

    private final HrEmployeeProfileService profileService;

    @PostMapping(ApiEndpoints.HR_EMPLOYEE_PROFILES)
    public ResponseEntity<ApiResponse<HrEmployeeProfileResponse>> createProfile(
            @Valid @RequestBody HrEmployeeProfileRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Create HR profile for empId: {} by: {}", request.getEmpId(), createdBy);
        HrEmployeeProfileResponse response = profileService.createProfile(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("HR profile created successfully", response));
    }

    @GetMapping(ApiEndpoints.HR_EMPLOYEE_PROFILES)
    public ResponseEntity<ApiResponse<PageResponse<HrEmployeeProfileResponse>>> getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String empId
    ) {
        log.info("Get all HR profiles - page: {}, size: {}, empId: {}", page, size, empId);

        Page<HrEmployeeProfileResponse> profiles = profileService.getAllProfiles(page, size, empId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "HR profiles retrieved successfully",
                        PageResponse.from(profiles)
                )
        );
    }

    @GetMapping(ApiEndpoints.HR_EMPLOYEES)
    public ResponseEntity<ApiResponse<PageResponse<HrEmployeeListResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search
    ) {
        log.info("Get all HR employees - page: {}, size: {}, search: {}", page, size, search);

        Page<HrEmployeeListResponse> employees = profileService.getAllEmployees(page, size, search);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "HR employees retrieved successfully",
                        PageResponse.from(employees)
                )
        );
    }

    @GetMapping(ApiEndpoints.HR_EMPLOYEE_PROFILE_BY_ID)
    public ResponseEntity<ApiResponse<HrEmployeeProfileResponse>> getProfileById(
            @PathVariable Long id
    ) {
        log.info("Get HR profile by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "HR profile retrieved successfully",
                        profileService.getProfileById(id)
                )
        );
    }

    @GetMapping(ApiEndpoints.HR_EMPLOYEE_PROFILE_BY_EMP)
    public ResponseEntity<ApiResponse<HrEmployeeProfileResponse>> getProfileByEmpId(
            @PathVariable String empId
    ) {
        log.info("Get HR profile by empId: {}", empId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "HR profile retrieved successfully",
                        profileService.getProfileByEmpId(empId)
                )
        );
    }

    @PutMapping(ApiEndpoints.HR_EMPLOYEE_PROFILE_BY_ID)
    public ResponseEntity<ApiResponse<HrEmployeeProfileResponse>> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody HrEmployeeProfileRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update HR profile ID: {} by: {}", id, updatedBy);
        HrEmployeeProfileResponse response = profileService.updateProfile(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("HR profile updated successfully", response));
    }

    @DeleteMapping(ApiEndpoints.HR_EMPLOYEE_PROFILE_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteProfile(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String deletedBy = authentication.getName();
        log.info("Soft delete HR profile ID: {} by: {}", id, deletedBy);
        profileService.deleteProfile(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("HR profile deactivated successfully"));
    }
}