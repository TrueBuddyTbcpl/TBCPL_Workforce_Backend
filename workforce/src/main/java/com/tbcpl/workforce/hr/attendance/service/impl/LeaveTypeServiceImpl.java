package com.tbcpl.workforce.hr.attendance.service.impl;

import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.attendance.dto.request.LeaveTypeRequest;
import com.tbcpl.workforce.hr.attendance.dto.response.LeaveTypeResponse;
import com.tbcpl.workforce.hr.attendance.entity.LeaveType;
import com.tbcpl.workforce.hr.attendance.repository.LeaveTypeRepository;
import com.tbcpl.workforce.hr.attendance.service.LeaveTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class LeaveTypeServiceImpl implements LeaveTypeService {

    private final LeaveTypeRepository         leaveTypeRepository;
    private final EmployeeNameResolverService  nameResolver;

    @Override
    @Transactional
    public LeaveTypeResponse createLeaveType(LeaveTypeRequest request, String createdBy) {
        log.info("Creating leave type: {}", request.getLeaveTypeName());

        if (leaveTypeRepository.existsByLeaveTypeNameIgnoreCase(request.getLeaveTypeName())) {
            throw new DuplicateResourceException(
                    "Leave type already exists: " + request.getLeaveTypeName());
        }

        LeaveType leaveType = LeaveType.builder()
                .leaveTypeName(request.getLeaveTypeName().trim())
                .category(request.getCategory())
                .description(request.getDescription())
                .maxDaysPerYear(request.getMaxDaysPerYear())
                .isCarryForwardAllowed(
                        request.getIsCarryForwardAllowed() != null
                                ? request.getIsCarryForwardAllowed() : false)
                .maxCarryForwardDays(request.getMaxCarryForwardDays())
                .isHalfDayAllowed(
                        request.getIsHalfDayAllowed() != null
                                ? request.getIsHalfDayAllowed() : true)
                .isPaid(request.getIsPaid() != null ? request.getIsPaid() : true)
                .minNoticeDays(
                        request.getMinNoticeDays() != null
                                ? request.getMinNoticeDays() : 0)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        LeaveType saved = leaveTypeRepository.save(leaveType);
        log.info("Leave type created with ID: {}", saved.getId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveTypeResponse getLeaveTypeById(Long id) {
        LeaveType lt = findById(id);
        return mapToResponse(lt, resolveCreatedBy(lt.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getAllActiveLeaveTypes() {
        List<LeaveType> types = leaveTypeRepository.findByIsActiveTrueOrderByLeaveTypeNameAsc();
        Map<String, String> nameMap = batchResolve(types.stream()
                .map(LeaveType::getCreatedBy).collect(Collectors.toSet()));
        return types.stream().map(lt -> mapToResponse(lt, nameMap)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getAllLeaveTypes() {
        List<LeaveType> types = leaveTypeRepository.findAll();
        Map<String, String> nameMap = batchResolve(types.stream()
                .map(LeaveType::getCreatedBy).collect(Collectors.toSet()));
        return types.stream().map(lt -> mapToResponse(lt, nameMap)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaveTypeResponse updateLeaveType(Long id, LeaveTypeRequest request, String updatedBy) {
        log.info("Updating leave type ID: {}", id);

        LeaveType leaveType = findById(id);

        if (leaveTypeRepository.existsByLeaveTypeNameIgnoreCaseAndIdNot(
                request.getLeaveTypeName(), id)) {
            throw new DuplicateResourceException(
                    "Leave type name already exists: " + request.getLeaveTypeName());
        }

        leaveType.setLeaveTypeName(request.getLeaveTypeName().trim());
        leaveType.setCategory(request.getCategory());
        leaveType.setDescription(request.getDescription());
        leaveType.setMaxDaysPerYear(request.getMaxDaysPerYear());
        if (request.getIsCarryForwardAllowed() != null)
            leaveType.setIsCarryForwardAllowed(request.getIsCarryForwardAllowed());
        if (request.getMaxCarryForwardDays() != null)
            leaveType.setMaxCarryForwardDays(request.getMaxCarryForwardDays());
        if (request.getIsHalfDayAllowed() != null)
            leaveType.setIsHalfDayAllowed(request.getIsHalfDayAllowed());
        if (request.getIsPaid() != null)
            leaveType.setIsPaid(request.getIsPaid());
        if (request.getMinNoticeDays() != null)
            leaveType.setMinNoticeDays(request.getMinNoticeDays());
        leaveType.setCreatedBy(updatedBy);

        LeaveType saved = leaveTypeRepository.save(leaveType);
        log.info("Leave type ID: {} updated", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteLeaveType(Long id) {
        log.info("Soft deleting leave type ID: {}", id);
        LeaveType leaveType = findById(id);
        leaveType.setIsActive(false);
        leaveTypeRepository.save(leaveType);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private LeaveType findById(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave type not found with ID: " + id));
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

    private LeaveTypeResponse mapToResponse(LeaveType lt, Map<String, String> nameMap) {
        String raw = lt.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return LeaveTypeResponse.builder()
                .id(lt.getId())
                .leaveTypeName(lt.getLeaveTypeName())
                .category(lt.getCategory())
                .description(lt.getDescription())
                .maxDaysPerYear(lt.getMaxDaysPerYear())
                .isCarryForwardAllowed(lt.getIsCarryForwardAllowed())
                .maxCarryForwardDays(lt.getMaxCarryForwardDays())
                .isHalfDayAllowed(lt.getIsHalfDayAllowed())
                .isPaid(lt.getIsPaid())
                .minNoticeDays(lt.getMinNoticeDays())
                .isActive(lt.getIsActive())
                .createdAt(lt.getCreatedAt())
                .updatedAt(lt.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}