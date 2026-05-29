package com.tbcpl.workforce.hr.performance.service.impl;

import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.performance.dto.request.HrAppraisalCycleRequest;
import com.tbcpl.workforce.hr.performance.dto.request.HrKraTemplateRequest;
import com.tbcpl.workforce.hr.performance.dto.response.HrAppraisalCycleResponse;
import com.tbcpl.workforce.hr.performance.dto.response.HrKraTemplateResponse;
import com.tbcpl.workforce.hr.performance.entity.HrAppraisalCycle;
import com.tbcpl.workforce.hr.performance.entity.HrKraTemplate;
import com.tbcpl.workforce.hr.performance.entity.enums.AppraisalStatus;
import com.tbcpl.workforce.hr.performance.entity.enums.KraStatus;
import com.tbcpl.workforce.hr.performance.repository.HrAppraisalCycleRepository;
import com.tbcpl.workforce.hr.performance.repository.HrKraTemplateRepository;
import com.tbcpl.workforce.hr.performance.service.HrAppraisalCycleService;
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
public class HrAppraisalCycleServiceImpl implements HrAppraisalCycleService {

    private final HrAppraisalCycleRepository cycleRepository;
    private final HrKraTemplateRepository    kraTemplateRepository;
    private final EmployeeNameResolverService nameResolver;

    // ── Appraisal Cycle ───────────────────────────────────────────────────────

    @Override
    @Transactional
    public HrAppraisalCycleResponse createCycle(HrAppraisalCycleRequest request,
                                                String createdBy) {
        log.info("Creating appraisal cycle: {} type:{} year:{}",
                request.getCycleName(), request.getCycleType(), request.getAppraisalYear());

        if (request.getPeriodEndDate().isBefore(request.getPeriodStartDate())) {
            throw new IllegalArgumentException(
                    "Period end date cannot be before period start date");
        }

        HrAppraisalCycle cycle = HrAppraisalCycle.builder()
                .cycleName(request.getCycleName().trim())
                .cycleType(request.getCycleType())
                .appraisalYear(request.getAppraisalYear())
                .periodStartDate(request.getPeriodStartDate())
                .periodEndDate(request.getPeriodEndDate())
                .selfReviewStartDate(request.getSelfReviewStartDate())
                .selfReviewEndDate(request.getSelfReviewEndDate())
                .managerReviewEndDate(request.getManagerReviewEndDate())
                .description(request.getDescription())
                .applicableDepartments(request.getApplicableDepartments() != null
                        ? request.getApplicableDepartments() : "ALL")
                .status(AppraisalStatus.DRAFT)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrAppraisalCycle saved = cycleRepository.save(cycle);
        log.info("Appraisal cycle created ID:{}", saved.getId());
        return mapCycleToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrAppraisalCycleResponse getCycleById(Long id) {
        HrAppraisalCycle cycle = findCycleById(id);
        return mapCycleToResponse(cycle, resolveCreatedBy(cycle.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrAppraisalCycleResponse> getAllCycles(int page, int size) {
        return cycleRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(c -> mapCycleToResponse(c, resolveCreatedBy(c.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrAppraisalCycleResponse> getCyclesByStatus(String status, int page, int size) {
        AppraisalStatus appraisalStatus = AppraisalStatus.valueOf(status.toUpperCase());
        return cycleRepository
                .findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
                        appraisalStatus, PageRequest.of(page, size))
                .map(c -> mapCycleToResponse(c, resolveCreatedBy(c.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrAppraisalCycleResponse> getCyclesByYear(Integer year) {
        return cycleRepository
                .findByAppraisalYearAndIsActiveTrueOrderByPeriodStartDateAsc(year)
                .stream()
                .map(c -> mapCycleToResponse(c, resolveCreatedBy(c.getCreatedBy())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HrAppraisalCycleResponse updateCycle(Long id, HrAppraisalCycleRequest request,
                                                String updatedBy) {
        log.info("Updating appraisal cycle ID:{} by:{}", id, updatedBy);
        HrAppraisalCycle cycle = findCycleById(id);

        if (cycle.getStatus() == AppraisalStatus.COMPLETED) {
            throw new IllegalStateException("Cannot update a COMPLETED appraisal cycle");
        }

        if (request.getCycleName()           != null) cycle.setCycleName(request.getCycleName().trim());
        if (request.getDescription()         != null) cycle.setDescription(request.getDescription());
        if (request.getPeriodStartDate()     != null) cycle.setPeriodStartDate(request.getPeriodStartDate());
        if (request.getPeriodEndDate()       != null) cycle.setPeriodEndDate(request.getPeriodEndDate());
        if (request.getSelfReviewStartDate() != null) cycle.setSelfReviewStartDate(request.getSelfReviewStartDate());
        if (request.getSelfReviewEndDate()   != null) cycle.setSelfReviewEndDate(request.getSelfReviewEndDate());
        if (request.getManagerReviewEndDate() != null) cycle.setManagerReviewEndDate(request.getManagerReviewEndDate());
        if (request.getApplicableDepartments() != null) cycle.setApplicableDepartments(request.getApplicableDepartments());
        cycle.setCreatedBy(updatedBy);

        HrAppraisalCycle saved = cycleRepository.save(cycle);
        return mapCycleToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrAppraisalCycleResponse updateCycleStatus(Long id, String status,
                                                      String updatedBy) {
        log.info("Updating cycle ID:{} status to:{} by:{}", id, status, updatedBy);
        HrAppraisalCycle cycle = findCycleById(id);
        cycle.setStatus(AppraisalStatus.valueOf(status.toUpperCase()));
        cycle.setCreatedBy(updatedBy);
        return mapCycleToResponse(cycleRepository.save(cycle),
                resolveCreatedBy(updatedBy));
    }

    @Override
    @Transactional
    public void deleteCycle(Long id) {
        log.info("Soft deleting appraisal cycle ID:{}", id);
        HrAppraisalCycle cycle = findCycleById(id);
        cycle.setIsActive(false);
        cycleRepository.save(cycle);
    }

    // ── KRA Templates ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public HrKraTemplateResponse createKraTemplate(HrKraTemplateRequest request,
                                                   String createdBy) {
        log.info("Creating KRA template: {} designation:{} dept:{}",
                request.getKraName(), request.getDesignation(), request.getDepartment());

        HrKraTemplate template = HrKraTemplate.builder()
                .kraName(request.getKraName().trim())
                .kraDescription(request.getKraDescription())
                .designation(request.getDesignation())
                .department(request.getDepartment())
                .weightage(request.getWeightage())
                .targetValue(request.getTargetValue())
                .measurementUnit(request.getMeasurementUnit())
                .status(request.getStatus() != null ? request.getStatus() : KraStatus.ACTIVE)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrKraTemplate saved = kraTemplateRepository.save(template);
        log.info("KRA template created ID:{}", saved.getId());
        return mapKraToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrKraTemplateResponse getKraTemplateById(Long id) {
        HrKraTemplate template = findKraById(id);
        return mapKraToResponse(template, resolveCreatedBy(template.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrKraTemplateResponse> getAllKraTemplates(int page, int size) {
        return kraTemplateRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(t -> mapKraToResponse(t, resolveCreatedBy(t.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrKraTemplateResponse> getKraTemplatesByDesignation(String designation) {
        return kraTemplateRepository
                .findByDesignationAndIsActiveTrueAndStatusOrderByWeightageDesc(
                        designation, KraStatus.ACTIVE)
                .stream()
                .map(t -> mapKraToResponse(t, resolveCreatedBy(t.getCreatedBy())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HrKraTemplateResponse updateKraTemplate(Long id, HrKraTemplateRequest request,
                                                   String updatedBy) {
        log.info("Updating KRA template ID:{} by:{}", id, updatedBy);
        HrKraTemplate template = findKraById(id);

        if (request.getKraName()         != null) template.setKraName(request.getKraName().trim());
        if (request.getKraDescription()  != null) template.setKraDescription(request.getKraDescription());
        if (request.getDesignation()     != null) template.setDesignation(request.getDesignation());
        if (request.getDepartment()      != null) template.setDepartment(request.getDepartment());
        if (request.getWeightage()       != null) template.setWeightage(request.getWeightage());
        if (request.getTargetValue()     != null) template.setTargetValue(request.getTargetValue());
        if (request.getMeasurementUnit() != null) template.setMeasurementUnit(request.getMeasurementUnit());
        if (request.getStatus()          != null) template.setStatus(request.getStatus());
        template.setCreatedBy(updatedBy);

        return mapKraToResponse(kraTemplateRepository.save(template),
                resolveCreatedBy(updatedBy));
    }

    @Override
    @Transactional
    public void deleteKraTemplate(Long id) {
        log.info("Soft deleting KRA template ID:{}", id);
        HrKraTemplate template = findKraById(id);
        template.setIsActive(false);
        kraTemplateRepository.save(template);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrAppraisalCycle findCycleById(Long id) {
        return cycleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appraisal cycle not found with ID: " + id));
    }

    private HrKraTemplate findKraById(Long id) {
        return kraTemplateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "KRA template not found with ID: " + id));
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return Collections.emptyMap();
        return nameResolver.resolve(Set.of(createdBy));
    }

    private HrAppraisalCycleResponse mapCycleToResponse(HrAppraisalCycle c,
                                                        Map<String, String> nameMap) {
        String raw = c.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HrAppraisalCycleResponse.builder()
                .id(c.getId())
                .cycleName(c.getCycleName())
                .cycleType(c.getCycleType())
                .appraisalYear(c.getAppraisalYear())
                .periodStartDate(c.getPeriodStartDate())
                .periodEndDate(c.getPeriodEndDate())
                .selfReviewStartDate(c.getSelfReviewStartDate())
                .selfReviewEndDate(c.getSelfReviewEndDate())
                .managerReviewEndDate(c.getManagerReviewEndDate())
                .status(c.getStatus())
                .description(c.getDescription())
                .applicableDepartments(c.getApplicableDepartments())
                .isActive(c.getIsActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }

    private HrKraTemplateResponse mapKraToResponse(HrKraTemplate t,
                                                   Map<String, String> nameMap) {
        String raw = t.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HrKraTemplateResponse.builder()
                .id(t.getId())
                .kraName(t.getKraName())
                .kraDescription(t.getKraDescription())
                .designation(t.getDesignation())
                .department(t.getDepartment())
                .weightage(t.getWeightage())
                .targetValue(t.getTargetValue())
                .measurementUnit(t.getMeasurementUnit())
                .status(t.getStatus())
                .isActive(t.getIsActive())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}