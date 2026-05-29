package com.tbcpl.workforce.hr.employeeprofile.service;

import com.tbcpl.workforce.hr.employeeprofile.dto.request.HrEmployeeProfileRequest;
import com.tbcpl.workforce.hr.employeeprofile.dto.response.HrEmployeeListResponse;
import com.tbcpl.workforce.hr.employeeprofile.dto.response.HrEmployeeProfileResponse;
import org.springframework.data.domain.Page;

public interface HrEmployeeProfileService {

    HrEmployeeProfileResponse createProfile(HrEmployeeProfileRequest request, String createdBy);

    HrEmployeeProfileResponse getProfileById(Long id);

    HrEmployeeProfileResponse getProfileByEmpId(String empId);

    Page<HrEmployeeProfileResponse> getAllProfiles(int page, int size, String empId);

    Page<HrEmployeeListResponse> getAllEmployees(int page, int size, String search);

    HrEmployeeProfileResponse updateProfile(Long id, HrEmployeeProfileRequest request, String updatedBy);

    void deleteProfile(Long id, String deletedBy);
}