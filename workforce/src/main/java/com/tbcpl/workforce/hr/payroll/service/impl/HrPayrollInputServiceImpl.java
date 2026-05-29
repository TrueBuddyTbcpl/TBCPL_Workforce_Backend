package com.tbcpl.workforce.hr.payroll.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.payroll.dto.request.HrPayrollInputRequest;
import com.tbcpl.workforce.hr.payroll.dto.response.HrPayrollInputResponse;
import com.tbcpl.workforce.hr.payroll.entity.HrPayrollInput;
import com.tbcpl.workforce.hr.payroll.entity.enums.PayrollInputType;
import com.tbcpl.workforce.hr.payroll.entity.enums.PayrollStatus;
import com.tbcpl.workforce.hr.payroll.repository.HrPayrollInputRepository;
import com.tbcpl.workforce.hr.payroll.service.HrPayrollInputService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrPayrollInputServiceImpl implements HrPayrollInputService {

    private final HrPayrollInputRepository    payrollInputRepository;
    private final EmployeeRepository          employeeRepository;
    private final EmployeeNameResolverService  nameResolver;

    @Override
    @Transactional
    public HrPayrollInputResponse addPayrollInput(HrPayrollInputRequest request,
                                                  String createdBy) {
        log.info("Adding payroll input for empId:{} month:{}/{} type:{}",
                request.getEmpId(), request.getPayrollMonth(),
                request.getPayrollYear(), request.getInputType());

        validateEmployeeExists(request.getEmpId());
        validateLwpFields(request);

        // Prevent duplicate input of same type for same month
        if (payrollInputRepository
                .existsByEmpIdAndPayrollMonthAndPayrollYearAndInputTypeAndIsActiveTrue(
                        request.getEmpId(), request.getPayrollMonth(),
                        request.getPayrollYear(), request.getInputType())) {
            throw new DuplicateResourceException(
                    "Payroll input of type " + request.getInputType()
                            + " already exists for empId: " + request.getEmpId()
                            + " for " + request.getPayrollMonth() + "/" + request.getPayrollYear()
                            + ". Use update instead.");
        }

        HrPayrollInput input = HrPayrollInput.builder()
                .empId(request.getEmpId().trim())
                .payrollMonth(request.getPayrollMonth())
                .payrollYear(request.getPayrollYear())
                .inputType(request.getInputType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .lwpDays(request.getLwpDays())
                .status(PayrollStatus.DRAFT)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrPayrollInput saved = payrollInputRepository.save(input);
        log.info("Payroll input created with ID: {}", saved.getId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrPayrollInputResponse getPayrollInputById(Long id) {
        HrPayrollInput input = findById(id);
        return mapToResponse(input, resolveCreatedBy(input.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrPayrollInputResponse> getPayrollInputsByEmpAndMonth(
            String empId, Integer month, Integer year) {
        log.info("Fetching payroll inputs for empId:{} month:{}/{}", empId, month, year);
        List<HrPayrollInput> inputs =
                payrollInputRepository
                        .findByEmpIdAndPayrollMonthAndPayrollYearAndIsActiveTrue(
                                empId, month, year);
        Map<String, String> nameMap = batchResolve(inputs.stream()
                .map(HrPayrollInput::getCreatedBy).collect(Collectors.toSet()));
        return inputs.stream()
                .map(i -> mapToResponse(i, nameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrPayrollInputResponse> getPayrollInputsByMonth(
            Integer month, Integer year, int page, int size) {
        log.info("Fetching all payroll inputs for month:{}/{} page:{} size:{}",
                month, year, page, size);
        return payrollInputRepository
                .findByPayrollMonthAndPayrollYearAndIsActiveTrueOrderByEmpIdAsc(
                        month, year,
                        PageRequest.of(page, size))
                .map(i -> mapToResponse(i, resolveCreatedBy(i.getCreatedBy())));
    }


    @Override
    @Transactional(readOnly = true)
    public Page<HrPayrollInputResponse> getPayrollInputsByEmpId(
            String empId, int page, int size) {
        log.info("Fetching payroll inputs for empId:{} page:{} size:{}", empId, page, size);
        return payrollInputRepository
                .findByEmpIdAndIsActiveTrueOrderByPayrollYearDescPayrollMonthDesc(
                        empId, PageRequest.of(page, size,
                                Sort.by("payrollYear").descending()
                                        .and(Sort.by("payrollMonth").descending())))
                .map(i -> mapToResponse(i, resolveCreatedBy(i.getCreatedBy())));
    }

    @Override
    @Transactional
    public HrPayrollInputResponse updatePayrollInput(Long id, HrPayrollInputRequest request,
                                                     String updatedBy) {
        log.info("Updating payroll input ID: {} by: {}", id, updatedBy);
        HrPayrollInput input = findById(id);

        if (input.getStatus() != PayrollStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only DRAFT payroll inputs can be updated. " +
                            "Current status: " + input.getStatus());
        }

        validateLwpFields(request);

        if (request.getAmount()      != null) input.setAmount(request.getAmount());
        if (request.getDescription() != null) input.setDescription(request.getDescription());
        if (request.getLwpDays()     != null) input.setLwpDays(request.getLwpDays());
        if (request.getInputType()   != null) input.setInputType(request.getInputType());
        input.setCreatedBy(updatedBy);

        HrPayrollInput saved = payrollInputRepository.save(input);
        log.info("Payroll input ID: {} updated", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public int submitPayrollInputsForMonth(Integer month, Integer year, String submittedBy) {
        log.info("Submitting payroll inputs for month:{}/{} by:{}", month, year, submittedBy);

        List<HrPayrollInput> draftInputs =
                payrollInputRepository.findDraftInputsForMonth(month, year);

        if (draftInputs.isEmpty()) {
            log.info("No DRAFT payroll inputs found for month:{}/{}", month, year);
            return 0;
        }

        LocalDateTime now = LocalDateTime.now();
        draftInputs.forEach(input -> {
            input.setStatus(PayrollStatus.SUBMITTED);
            input.setSubmittedAt(now);
            input.setSubmittedBy(submittedBy);
        });

        payrollInputRepository.saveAll(draftInputs);
        log.info("Submitted {} payroll inputs for month:{}/{}", draftInputs.size(), month, year);
        return draftInputs.size();
    }

    @Override
    @Transactional
    public void deletePayrollInput(Long id) {
        log.info("Soft deleting payroll input ID: {}", id);
        HrPayrollInput input = findById(id);
        if (input.getStatus() != PayrollStatus.DRAFT) {
            throw new IllegalStateException(
                    "Only DRAFT payroll inputs can be deleted.");
        }
        input.setIsActive(false);
        payrollInputRepository.save(input);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrPayrollInput findById(Long id) {
        return payrollInputRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payroll input not found with ID: " + id));
    }

    private void validateEmployeeExists(String empId) {
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
        }
    }

    private void validateLwpFields(HrPayrollInputRequest request) {
        if (request.getInputType() == PayrollInputType.LEAVE_WITHOUT_PAY
                && (request.getLwpDays() == null || request.getLwpDays() <= 0)) {
            throw new IllegalArgumentException(
                    "LWP days are required and must be greater than 0 "
                            + "when input type is LEAVE_WITHOUT_PAY");
        }
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

    private HrPayrollInputResponse mapToResponse(HrPayrollInput i,
                                                 Map<String, String> nameMap) {
        String raw = i.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(
                        raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HrPayrollInputResponse.builder()
                .id(i.getId())
                .empId(i.getEmpId())
                .payrollMonth(i.getPayrollMonth())
                .payrollYear(i.getPayrollYear())
                .inputType(i.getInputType())
                .amount(i.getAmount())
                .description(i.getDescription())
                .lwpDays(i.getLwpDays())
                .status(i.getStatus())
                .submittedAt(i.getSubmittedAt())
                .submittedBy(i.getSubmittedBy())
                .isActive(i.getIsActive())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}