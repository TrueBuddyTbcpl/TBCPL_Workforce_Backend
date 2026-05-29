package com.tbcpl.workforce.hr.attendance.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.attendance.dto.request.AttendanceRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.AttendanceResponse;
import com.tbcpl.workforce.hr.attendance.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.HR_BASE)
@RequiredArgsConstructor
@Slf4j
public class AttendanceController {

    private final AttendanceService attendanceService;

    /** POST /api/v1/hr/attendance */
    @PostMapping(ApiEndpoints.HR_ATTENDANCE)
    public ResponseEntity<ApiResponse<AttendanceResponse>> markAttendance(
            @Valid @RequestBody AttendanceRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Mark attendance for empId: {} by: {}", request.getEmpId(), createdBy);
        AttendanceResponse response = attendanceService.markAttendance(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attendance marked successfully", response));
    }

    /** GET /api/v1/hr/attendance/{id} */
    @GetMapping(ApiEndpoints.HR_ATTENDANCE_BY_ID)
    public ResponseEntity<ApiResponse<AttendanceResponse>> getAttendanceById(
            @PathVariable Long id
    ) {
        log.info("Get attendance by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("Attendance record retrieved",
                        attendanceService.getAttendanceById(id)));
    }

    /** GET /api/v1/hr/attendance/emp/{empId}?page=0&size=20 */
    @GetMapping(ApiEndpoints.HR_ATTENDANCE_BY_EMP)
    public ResponseEntity<ApiResponse<Page<AttendanceResponse>>> getAttendanceByEmpId(
            @PathVariable String empId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Get attendance for empId: {} page:{} size:{}", empId, page, size);
        Page<AttendanceResponse> records =
                attendanceService.getAttendanceByEmpId(empId, page, size);
        return ResponseEntity.ok(
                ApiResponse.success("Attendance records retrieved", records));
    }

    /** GET /api/v1/hr/attendance/emp/{empId}/monthly?year=2026&month=5 */
    @GetMapping(ApiEndpoints.HR_ATTENDANCE_MONTHLY)
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getMonthlyAttendance(
            @PathVariable String empId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        log.info("Get monthly attendance for empId:{} year:{} month:{}", empId, year, month);
        List<AttendanceResponse> records =
                attendanceService.getMonthlyAttendance(empId, year, month);
        return ResponseEntity.ok(
                ApiResponse.success("Monthly attendance retrieved", records));
    }

    /** PUT /api/v1/hr/attendance/{id} */
    @PutMapping(ApiEndpoints.HR_ATTENDANCE_BY_ID)
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable Long id,
            @Valid @RequestBody AttendanceRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update attendance ID: {} by: {}", id, updatedBy);
        AttendanceResponse response = attendanceService.updateAttendance(id, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", response));
    }

    /** DELETE /api/v1/hr/attendance/{id} */
    @DeleteMapping(ApiEndpoints.HR_ATTENDANCE_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String deletedBy = authentication.getName();
        log.info("Soft delete attendance ID: {} by: {}", id, deletedBy);
        attendanceService.deleteAttendance(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("Attendance record deleted successfully"));
    }
}