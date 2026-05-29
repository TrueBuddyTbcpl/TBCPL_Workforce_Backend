package com.tbcpl.workforce.hr.attendance.service;

import com.tbcpl.workforce.hr.attendance.dto.request.LeaveTypeRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.LeaveTypeResponse;

import java.util.List;

public interface LeaveTypeService {

    LeaveTypeResponse createLeaveType(LeaveTypeRequest request, String createdBy);

    LeaveTypeResponse getLeaveTypeById(Long id);

    List<LeaveTypeResponse> getAllActiveLeaveTypes();

    List<LeaveTypeResponse> getAllLeaveTypes();

    LeaveTypeResponse updateLeaveType(Long id, LeaveTypeRequest request, String updatedBy);

    void deleteLeaveType(Long id);
}