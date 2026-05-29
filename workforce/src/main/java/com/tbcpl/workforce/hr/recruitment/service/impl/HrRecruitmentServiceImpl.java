package com.tbcpl.workforce.hr.recruitment.service.impl;

import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrJobRequisitionRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrJobRequisitionResponse;
import com.tbcpl.workforce.hr.recruitment.entity.HrJobRequisition;
import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import com.tbcpl.workforce.hr.recruitment.repository.HrJobRequisitionRepository;
import com.tbcpl.workforce.hr.recruitment.service.HrRecruitmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrRecruitmentServiceImpl implements HrRecruitmentService {

    private final HrJobRequisitionRepository requisitionRepository;
    private final EmployeeNameResolverService  nameResolver;

    @Override
    @Transactional
    public HrJobRequisitionResponse createRequisition(HrJobRequisitionRequest request,
                                                      String createdBy) {
        log.info("Creating job requisition for: {} dept: {}",
                request.getJobTitle(), request.getDepartment());

        String code = generateRequisitionCode();

        HrJobRequisition requisition = HrJobRequisition.builder()
                .requisitionCode(code)
                .jobTitle(request.getJobTitle().trim())
                .department(request.getDepartment().trim())
                .designation(request.getDesignation())
                .numberOfPositions(request.getNumberOfPositions())
                .filledPositions(0)
                .jobDescription(request.getJobDescription())
                .requiredExperienceYears(request.getRequiredExperienceYears())
                .requiredSkills(request.getRequiredSkills())
                .minSalaryBudget(request.getMinSalaryBudget())
                .maxSalaryBudget(request.getMaxSalaryBudget())
                .targetJoiningDate(request.getTargetJoiningDate())
                .raisedBy(createdBy)
                .status(RecruitmentStatus.OPEN)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrJobRequisition saved = requisitionRepository.save(requisition);
        log.info("Job requisition created: {} ID: {}", code, saved.getId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrJobRequisitionResponse getRequisitionById(Long id) {
        HrJobRequisition r = findById(id);
        return mapToResponse(r, resolveCreatedBy(r.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrJobRequisitionResponse getRequisitionByCode(String code) {
        HrJobRequisition r = requisitionRepository
                .findByRequisitionCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Requisition not found with code: " + code));
        return mapToResponse(r, resolveCreatedBy(r.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrJobRequisitionResponse> getAllRequisitions(int page, int size) {
        return requisitionRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(r -> mapToResponse(r, resolveCreatedBy(r.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrJobRequisitionResponse> getRequisitionsByStatus(
            String status, int page, int size) {
        RecruitmentStatus recruitmentStatus = RecruitmentStatus.valueOf(status.toUpperCase());
        return requisitionRepository
                .findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
                        recruitmentStatus,
                        PageRequest.of(page, size))
                .map(r -> mapToResponse(r, resolveCreatedBy(r.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrJobRequisitionResponse> getRequisitionsByDepartment(
            String department, int page, int size) {
        return requisitionRepository
                .findByDepartmentAndIsActiveTrueOrderByCreatedAtDesc(
                        department, PageRequest.of(page, size))
                .map(r -> mapToResponse(r, resolveCreatedBy(r.getCreatedBy())));
    }

    @Override
    @Transactional
    public HrJobRequisitionResponse updateRequisition(Long id,
                                                      HrJobRequisitionRequest request,
                                                      String updatedBy) {
        log.info("Updating requisition ID: {} by: {}", id, updatedBy);
        HrJobRequisition r = findById(id);

        if (request.getJobTitle()    != null) r.setJobTitle(request.getJobTitle().trim());
        if (request.getDepartment()  != null) r.setDepartment(request.getDepartment());
        if (request.getDesignation() != null) r.setDesignation(request.getDesignation());
        if (request.getNumberOfPositions() != null)
            r.setNumberOfPositions(request.getNumberOfPositions());
        if (request.getJobDescription()   != null) r.setJobDescription(request.getJobDescription());
        if (request.getRequiredSkills()   != null) r.setRequiredSkills(request.getRequiredSkills());
        if (request.getMinSalaryBudget()  != null) r.setMinSalaryBudget(request.getMinSalaryBudget());
        if (request.getMaxSalaryBudget()  != null) r.setMaxSalaryBudget(request.getMaxSalaryBudget());
        if (request.getTargetJoiningDate() != null) r.setTargetJoiningDate(request.getTargetJoiningDate());
        if (request.getRequiredExperienceYears() != null)
            r.setRequiredExperienceYears(request.getRequiredExperienceYears());
        r.setCreatedBy(updatedBy);

        HrJobRequisition saved = requisitionRepository.save(r);
        log.info("Requisition ID: {} updated", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrJobRequisitionResponse updateRequisitionStatus(Long id, String status,
                                                            String remarks, String updatedBy) {
        log.info("Updating requisition ID: {} status to: {} by: {}", id, status, updatedBy);
        HrJobRequisition r = findById(id);
        r.setStatus(RecruitmentStatus.valueOf(status.toUpperCase()));
        if (remarks != null) r.setClosureRemarks(remarks);
        r.setCreatedBy(updatedBy);
        HrJobRequisition saved = requisitionRepository.save(r);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteRequisition(Long id) {
        log.info("Soft deleting requisition ID: {}", id);
        HrJobRequisition r = findById(id);
        r.setIsActive(false);
        requisitionRepository.save(r);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrJobRequisition findById(Long id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job requisition not found with ID: " + id));
    }

    /**
     * Generates requisition code: JR-{YEAR}-{SEQ}
     * e.g. JR-2026-001, JR-2026-002
     */
    private String generateRequisitionCode() {
        String year   = String.valueOf(Year.now().getValue());
        String prefix = "JR-" + year + "-";
        List<String> codes = requisitionRepository.findLastCodeByPrefix(prefix);

        int nextSeq = 1;
        if (!codes.isEmpty()) {
            String last = codes.get(0);
            try {
                nextSeq = Integer.parseInt(last.substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {}
        }
        String code = prefix + String.format("%03d", nextSeq);

        if (requisitionRepository.existsByRequisitionCode(code)) {
            code = prefix + String.format("%03d", nextSeq + 1);
        }
        return code;
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return Collections.emptyMap();
        return nameResolver.resolve(Set.of(createdBy));
    }

    private HrJobRequisitionResponse mapToResponse(HrJobRequisition r,
                                                   Map<String, String> nameMap) {
        String raw = r.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HrJobRequisitionResponse.builder()
                .id(r.getId())
                .requisitionCode(r.getRequisitionCode())
                .jobTitle(r.getJobTitle())
                .department(r.getDepartment())
                .designation(r.getDesignation())
                .numberOfPositions(r.getNumberOfPositions())
                .filledPositions(r.getFilledPositions())
                .remainingPositions(r.getNumberOfPositions() - r.getFilledPositions())
                .jobDescription(r.getJobDescription())
                .requiredExperienceYears(r.getRequiredExperienceYears())
                .requiredSkills(r.getRequiredSkills())
                .minSalaryBudget(r.getMinSalaryBudget())
                .maxSalaryBudget(r.getMaxSalaryBudget())
                .targetJoiningDate(r.getTargetJoiningDate())
                .raisedBy(r.getRaisedBy())
                .status(r.getStatus())
                .closureRemarks(r.getClosureRemarks())
                .isActive(r.getIsActive())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}