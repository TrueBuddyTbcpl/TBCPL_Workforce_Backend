package com.tbcpl.workforce.hr.attendance.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.attendance.dto.request.AttendanceRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.AttendanceResponse;
import com.tbcpl.workforce.hr.attendance.entity.Attendance;
import com.tbcpl.workforce.hr.attendance.repository.AttendanceRepository;
import com.tbcpl.workforce.hr.attendance.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository        attendanceRepository;
    private final EmployeeRepository          employeeRepository;
    private final EmployeeNameResolverService  nameResolver;

    @Override
    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest request, String createdBy) {
        log.info("Marking attendance for empId: {} date: {}",
                request.getEmpId(), request.getAttendanceDate());

        validateEmployeeExists(request.getEmpId());

        if (attendanceRepository.existsByEmpIdAndAttendanceDate(
                request.getEmpId(), request.getAttendanceDate())) {
            throw new DuplicateResourceException(
                    "Attendance already marked for empId: " + request.getEmpId()
                            + " on date: " + request.getAttendanceDate()
                            + ". Use update to modify.");
        }

        Attendance attendance = Attendance.builder()
                .empId(request.getEmpId().trim())
                .attendanceDate(request.getAttendanceDate())
                .status(request.getStatus())
                .punchInTime(request.getPunchInTime())
                .punchOutTime(request.getPunchOutTime())
                .workingHours(calculateWorkingHours(request))
                .isRegularized(
                        request.getIsRegularized() != null
                                ? request.getIsRegularized() : false)
                .regularizationReason(request.getRegularizationReason())
                .remarks(request.getRemarks())
                .isActive(true)
                .createdBy(createdBy)
                .build();

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Attendance marked with ID: {}", saved.getId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceById(Long id) {
        Attendance a = findById(id);
        return mapToResponse(a, resolveCreatedBy(a.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceResponse> getAttendanceByEmpId(String empId, int page, int size) {
        log.info("Fetching attendance for empId: {} page:{} size:{}", empId, page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("attendanceDate").descending());
        Page<Attendance> records = attendanceRepository
                .findByEmpIdAndIsActiveTrue(empId, pageable);

        Set<String> createdBySet = records.stream()
                .map(Attendance::getCreatedBy)
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.toSet());
        Map<String, String> nameMap = createdBySet.isEmpty()
                ? Collections.emptyMap() : nameResolver.resolve(createdBySet);

        return records.map(a -> mapToResponse(a, nameMap));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getMonthlyAttendance(String empId, int year, int month) {
        log.info("Fetching monthly attendance for empId:{} year:{} month:{}", empId, year, month);
        validateEmployeeExists(empId);
        List<Attendance> records =
                attendanceRepository.findMonthlyAttendance(empId, year, month);
        Map<String, String> nameMap = batchResolve(
                records.stream().map(Attendance::getCreatedBy).collect(Collectors.toSet()));
        return records.stream()
                .map(a -> mapToResponse(a, nameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AttendanceResponse updateAttendance(Long id, AttendanceRequest request,
                                               String updatedBy) {
        log.info("Updating attendance ID: {} by: {}", id, updatedBy);
        Attendance attendance = findById(id);

        if (request.getStatus()       != null) attendance.setStatus(request.getStatus());
        if (request.getPunchInTime()  != null) attendance.setPunchInTime(request.getPunchInTime());
        if (request.getPunchOutTime() != null) attendance.setPunchOutTime(request.getPunchOutTime());
        if (request.getWorkingHours() != null) attendance.setWorkingHours(request.getWorkingHours());
        else attendance.setWorkingHours(calculateWorkingHours(request));
        if (request.getIsRegularized() != null) attendance.setIsRegularized(request.getIsRegularized());
        if (request.getRegularizationReason() != null)
            attendance.setRegularizationReason(request.getRegularizationReason());
        if (request.getRemarks() != null) attendance.setRemarks(request.getRemarks());
        attendance.setCreatedBy(updatedBy);

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Attendance ID: {} updated", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteAttendance(Long id, String deletedBy) {
        log.info("Soft deleting attendance ID: {} by: {}", id, deletedBy);
        Attendance attendance = findById(id);
        attendance.setIsActive(false);
        attendanceRepository.save(attendance);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Attendance findById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Attendance record not found with ID: " + id));
    }

    private void validateEmployeeExists(String empId) {
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
        }
    }

    /**
     * Auto-calculate working hours from punch-in/out if not explicitly provided.
     * Returns null if either time is missing.
     */
    private Double calculateWorkingHours(AttendanceRequest request) {
        if (request.getWorkingHours() != null) return request.getWorkingHours();
        if (request.getPunchInTime() != null && request.getPunchOutTime() != null) {
            long minutes = java.time.Duration.between(
                    request.getPunchInTime(), request.getPunchOutTime()).toMinutes();
            return minutes > 0 ? Math.round(minutes / 60.0 * 100.0) / 100.0 : null;
        }
        return null;
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return Collections.emptyMap();
        return nameResolver.resolve(Set.of(createdBy));
    }

    private Map<String, String> batchResolve(Set<String> values) {
        Set<String> filtered = values.stream()
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.toSet());
        return filtered.isEmpty() ? Collections.emptyMap() : nameResolver.resolve(filtered);
    }

    private AttendanceResponse mapToResponse(Attendance a, Map<String, String> nameMap) {
        String raw = a.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return AttendanceResponse.builder()
                .id(a.getId())
                .empId(a.getEmpId())
                .attendanceDate(a.getAttendanceDate())
                .status(a.getStatus())
                .punchInTime(a.getPunchInTime())
                .punchOutTime(a.getPunchOutTime())
                .workingHours(a.getWorkingHours())
                .isRegularized(a.getIsRegularized())
                .regularizationReason(a.getRegularizationReason())
                .remarks(a.getRemarks())
                .isActive(a.getIsActive())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}