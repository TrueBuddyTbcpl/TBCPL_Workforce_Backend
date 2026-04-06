package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.service.EmployeeService;
import com.tbcpl.workforce.common.enums.DepartmentType;
import com.tbcpl.workforce.common.enums.RoleType;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.exception.UnauthorizedAccessException;
import com.tbcpl.workforce.operation.prereport.dto.request.CustomOptClientLeadRequest;
import com.tbcpl.workforce.operation.prereport.dto.response.CustomOptClientLeadResponse;
import com.tbcpl.workforce.operation.prereport.entity.PrereportCustomOptClientLead;
import com.tbcpl.workforce.operation.prereport.repository.PrereportCustomOptClientLeadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PrereportCustomOptClientLeadService {

    private final PrereportCustomOptClientLeadRepository repository;
    private final EmployeeService employeeService;

    public PrereportCustomOptClientLeadService(
            PrereportCustomOptClientLeadRepository repository,
            EmployeeService employeeService) {
        this.repository      = repository;
        this.employeeService = employeeService;
    }

    // ── GET by step (Client Lead) ─────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<CustomOptClientLeadResponse> getOptionsByStep(Integer stepNumber, String leadType) {
        log.info("Fetching custom options for step: {}, leadType: {}", stepNumber, leadType);
        return repository
                .findByStepNumberAndLeadTypeAndDeletedFalseOrderByCreatedAtAsc(stepNumber, leadType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ← ADD: GET by fieldKey (TrueBuddy Lead) ─────────────────────────────────

    @Transactional(readOnly = true)
    public List<CustomOptClientLeadResponse> getOptionsByFieldKey(String fieldKey, String leadType) {
        log.info("Fetching custom options for fieldKey: {}, leadType: {}", fieldKey, leadType);
        return repository
                .findByFieldKeyAndLeadTypeAndDeletedFalseOrderByCreatedAtAsc(fieldKey, leadType)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── CREATE — all authenticated users ─────────────────────────────────────

    @Transactional
    public CustomOptClientLeadResponse createOption(CustomOptClientLeadRequest request) {
        String empId = resolveCurrentEmpId();
        log.info("Creating custom option for step {} by empId: {}", request.getStepNumber(), empId);

        PrereportCustomOptClientLead option = PrereportCustomOptClientLead.builder()
                .stepNumber(request.getStepNumber())
                .optionName(request.getOptionName().trim())
                .optionDescription(request.getOptionDescription() != null
                        ? request.getOptionDescription().trim() : null)
                .createdBy(empId)
                .leadType(request.getLeadType())
                .fieldKey(request.getFieldKey())   // ← ADD
                .build();

        return toResponse(repository.save(option));
    }

    // ── DELETE — Admin dept + Admin/SuperAdmin role only ──────────────────────

    @Transactional
    public void deleteOption(Long id) {
        String empId = resolveCurrentEmpId();
        log.info("Delete request for custom option id: {} by empId: {}", id, empId);

        assertAdminAccess(empId);

        PrereportCustomOptClientLead option = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Custom option not found with id: " + id));

        option.setDeleted(true);
        repository.save(option);
        log.info("Custom option id: {} soft-deleted by empId: {}", id, empId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void assertAdminAccess(String empId) {
        Employee employee = employeeService.getEmployeeEntityByEmpId(empId);
        String deptName = employee.getDepartment().getDepartmentName();
        String roleName = employee.getRole().getRoleName();

        boolean isAdminDept  = DepartmentType.ADMIN == DepartmentType.fromString(deptName);
        boolean isAdminRole  = RoleType.ADMIN       == RoleType.fromDbValue(roleName)
                || RoleType.SUPER_ADMIN == RoleType.fromDbValue(roleName);

        if (!isAdminDept || !isAdminRole) {
            throw new UnauthorizedAccessException(
                    "Only Admin department users with Admin or Super Admin role can delete custom options");
        }
    }

    private String resolveCurrentEmpId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new UnauthorizedAccessException("Not authenticated");
        }
        return auth.getName();
    }

    private CustomOptClientLeadResponse toResponse(PrereportCustomOptClientLead o) {
        return CustomOptClientLeadResponse.builder()
                .id(o.getId())
                .stepNumber(o.getStepNumber())
                .optionName(o.getOptionName())
                .optionDescription(o.getOptionDescription())
                .createdBy(o.getCreatedBy())
                .createdAt(o.getCreatedAt())
                .build();
    }
}