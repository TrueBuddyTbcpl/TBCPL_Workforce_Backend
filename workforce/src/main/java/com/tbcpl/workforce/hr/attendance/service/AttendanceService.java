package com.tbcpl.workforce.hr.attendance.service;

import com.tbcpl.workforce.hr.attendance.dto.request.AttendanceRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.AttendanceResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AttendanceService {

    AttendanceResponse markAttendance(AttendanceRequest request, String createdBy);

    AttendanceResponse getAttendanceById(Long id);

    Page<AttendanceResponse> getAttendanceByEmpId(String empId, int page, int size);

    List<AttendanceResponse> getMonthlyAttendance(String empId, int year, int month);

    AttendanceResponse updateAttendance(Long id, AttendanceRequest request, String updatedBy);

    void deleteAttendance(Long id, String deletedBy);
}