package com.tbcpl.workforce.hr.payroll.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.payroll.dto.request.HrSalaryComponentRequest;
import com.tbcpl.workforce.hr.payroll.dto.request.HrSalaryStructureRequest;
import com.tbcpl.workforce.hr.payroll.dto.response.HrSalaryComponentResponse;
import com.tbcpl.workforce.hr.payroll.dto.response.HrSalaryStructureResponse;
import com.tbcpl.workforce.hr.payroll.entity.HrSalaryComponent;
import com.tbcpl.workforce.hr.payroll.entity.HrSalaryStructure;
import com.tbcpl.workforce.hr.payroll.entity.enums.SalaryComponentType;
import com.tbcpl.workforce.hr.payroll.repository.HrSalaryStructureRepository;
import com.tbcpl.workforce.hr.payroll.service.HrSalaryStructureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrSalaryStructureServiceImpl implements HrSalaryStructureService {

    private final HrSalaryStructureRepository salaryStructureRepository;
    private final EmployeeRepository          employeeRepository;
    private final EmployeeNameResolverService  nameResolver;

    @Override
    @Transactional
    public HrSalaryStructureResponse createSalaryStructure(HrSalaryStructureRequest request,
                                                           String createdBy) {
        log.info("Creating salary structure for empId: {} effectiveFrom: {}",
                request.getEmpId(), request.getEffectiveFrom());

        if (!employeeRepository.existsByEmpId(request.getEmpId())) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + request.getEmpId());
        }

        // Close any existing open structure
        salaryStructureRepository.findOpenStructureByEmpId(request.getEmpId())
                .ifPresent(existing -> {
                    existing.setEffectiveTo(request.getEffectiveFrom().minusDays(1));
                    salaryStructureRepository.save(existing);
                    log.info("Closed existing salary structure ID: {} for empId: {}",
                            existing.getId(), request.getEmpId());
                });

        double annualCtc    = request.getAnnualCtc();
        double monthlyGross = Math.round((annualCtc / 12.0) * 100.0) / 100.0;

        HrSalaryStructure structure = HrSalaryStructure.builder()
                .empId(request.getEmpId().trim())
                .annualCtc(annualCtc)
                .monthlyGross(monthlyGross)
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTo(null)
                .revisionRemarks(request.getRevisionRemarks())
                .isActive(true)
                .createdBy(createdBy)
                .build();

        // Build and attach components
        List<HrSalaryComponent> components = buildComponents(
                request.getComponents(), structure, annualCtc, createdBy);
        structure.setComponents(components);

        HrSalaryStructure saved = salaryStructureRepository.save(structure);
        log.info("Salary structure created with ID: {} for empId: {}",
                saved.getId(), request.getEmpId());

        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrSalaryStructureResponse getCurrentStructure(String empId) {
        log.info("Fetching current salary structure for empId: {}", empId);
        HrSalaryStructure structure = salaryStructureRepository
                .findCurrentStructureByEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active salary structure found for empId: " + empId));
        return mapToResponse(structure, resolveCreatedBy(structure.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrSalaryStructureResponse> getSalaryHistory(String empId) {
        log.info("Fetching salary history for empId: {}", empId);
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
        }
        List<HrSalaryStructure> structures =
                salaryStructureRepository.findAllByEmpIdWithComponents(empId);
        Map<String, String> nameMap = batchResolve(structures.stream()
                .map(HrSalaryStructure::getCreatedBy).collect(Collectors.toSet()));
        return structures.stream()
                .map(s -> mapToResponse(s, nameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public HrSalaryStructureResponse getSalaryStructureById(Long id) {
        HrSalaryStructure structure = findById(id);
        return mapToResponse(structure, resolveCreatedBy(structure.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrSalaryStructureResponse> getAllSalaryStructures(int page, int size) {
        log.info("Fetching all salary structures page:{} size:{}", page, size);
        return salaryStructureRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(s -> mapToResponse(s, resolveCreatedBy(s.getCreatedBy())));
    }

    @Override
    @Transactional
    public void deleteSalaryStructure(Long id) {
        log.info("Soft deleting salary structure ID: {}", id);
        HrSalaryStructure structure = findById(id);
        structure.setIsActive(false);
        salaryStructureRepository.save(structure);
    }

    @Override
    @Transactional(readOnly = true)
    public HrSalaryStructureResponse getSalaryStructureByEmpId(String empId) {
        log.info("Fetching active salary structure for empId:{}", empId);
        HrSalaryStructure structure = salaryStructureRepository
                .findLatestByEmpId(empId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No active salary structure found for empId: " + empId));
        return mapToResponse(structure, resolveCreatedBy(structure.getCreatedBy()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrSalaryStructure findById(Long id) {
        return salaryStructureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Salary structure not found with ID: " + id));
    }

    /**
     * Build salary component entities from request.
     * Calculates monthly and annual amounts based on calculation type.
     */
    private List<HrSalaryComponent> buildComponents(
            List<HrSalaryComponentRequest> requests,
            HrSalaryStructure structure,
            double annualCtc,
            String createdBy) {

        double basicMonthly = requests.stream()
                .filter(r -> r.getComponentName().equalsIgnoreCase("Basic"))
                .findFirst()
                .map(r -> computeMonthlyAmount(r, annualCtc))
                .orElse(0.0);

        List<HrSalaryComponent> components = new ArrayList<>();
        for (HrSalaryComponentRequest req : requests) {
            double monthly = computeMonthlyAmountWithBasic(req, annualCtc, basicMonthly);
            double annual  = Math.round(monthly * 12 * 100.0) / 100.0;

            components.add(HrSalaryComponent.builder()
                    .salaryStructure(structure)
                    .componentName(req.getComponentName().trim())
                    .componentType(req.getComponentType())
                    .calculationType(req.getCalculationType())
                    .percentageValue(req.getPercentageValue())
                    .flatAmount(req.getFlatAmount())
                    .monthlyAmount(monthly)
                    .annualAmount(annual)
                    .isStatutory(req.getIsStatutory() != null
                            ? req.getIsStatutory() : false)
                    .displayOrder(req.getDisplayOrder() != null
                            ? req.getDisplayOrder() : 0)
                    .isActive(true)
                    .createdBy(createdBy)
                    .build());
        }
        return components;
    }

    private double computeMonthlyAmount(HrSalaryComponentRequest req, double annualCtc) {
        return computeMonthlyAmountWithBasic(req, annualCtc, 0.0);
    }

    private double computeMonthlyAmountWithBasic(HrSalaryComponentRequest req,
                                                 double annualCtc,
                                                 double basicMonthly) {
        double monthly = switch (req.getCalculationType()) {
            case FLAT_AMOUNT -> req.getFlatAmount() != null ? req.getFlatAmount() : 0.0;
            case PERCENTAGE_OF_CTC -> req.getPercentageValue() != null
                    ? (annualCtc * req.getPercentageValue() / 100.0) / 12.0
                    : 0.0;
            case PERCENTAGE_OF_BASIC -> req.getPercentageValue() != null
                    ? basicMonthly * req.getPercentageValue() / 100.0
                    : 0.0;
            case STATUTORY -> req.getFlatAmount() != null ? req.getFlatAmount() : 0.0;
        };
        return Math.round(monthly * 100.0) / 100.0;
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

    private HrSalaryStructureResponse mapToResponse(HrSalaryStructure s,
                                                    Map<String, String> nameMap) {
        String raw = s.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        List<HrSalaryComponentResponse> componentResponses = s.getComponents() == null
                ? Collections.emptyList()
                : s.getComponents().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .sorted(Comparator.comparingInt(c ->
                        c.getDisplayOrder() != null ? c.getDisplayOrder() : 0))
                .map(this::mapComponentToResponse)
                .collect(Collectors.toList());

        double totalEarnings = componentResponses.stream()
                .filter(c -> c.getComponentType() == SalaryComponentType.EARNING)
                .mapToDouble(c -> c.getMonthlyAmount() != null ? c.getMonthlyAmount() : 0.0)
                .sum();

        double totalDeductions = componentResponses.stream()
                .filter(c -> c.getComponentType() == SalaryComponentType.DEDUCTION)
                .mapToDouble(c -> c.getMonthlyAmount() != null ? c.getMonthlyAmount() : 0.0)
                .sum();

        return HrSalaryStructureResponse.builder()
                .id(s.getId())
                .empId(s.getEmpId())
                .annualCtc(s.getAnnualCtc())
                .monthlyGross(s.getMonthlyGross())
                .effectiveFrom(s.getEffectiveFrom())
                .effectiveTo(s.getEffectiveTo())
                .revisionRemarks(s.getRevisionRemarks())
                .isActive(s.getIsActive())
                .components(componentResponses)
                .totalMonthlyEarnings(Math.round(totalEarnings * 100.0) / 100.0)
                .totalMonthlyDeductions(Math.round(totalDeductions * 100.0) / 100.0)
                .netMonthlySalary(Math.round((totalEarnings - totalDeductions) * 100.0) / 100.0)
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }

    private HrSalaryComponentResponse mapComponentToResponse(HrSalaryComponent c) {
        return HrSalaryComponentResponse.builder()
                .id(c.getId())
                .componentName(c.getComponentName())
                .componentType(c.getComponentType())
                .calculationType(c.getCalculationType())
                .percentageValue(c.getPercentageValue())
                .flatAmount(c.getFlatAmount())
                .monthlyAmount(c.getMonthlyAmount())
                .annualAmount(c.getAnnualAmount())
                .isStatutory(c.getIsStatutory())
                .displayOrder(c.getDisplayOrder())
                .isActive(c.getIsActive())
                .build();
    }
}