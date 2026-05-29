package com.tbcpl.workforce.hr.performance.service;

import com.tbcpl.workforce.hr.performance.dto.request.HrAppraisalCycleRequest;
import com.tbcpl.workforce.hr.performance.dto.request.HrKraTemplateRequest;
import com.tbcpl.workforce.hr.performance.dto.response.HrAppraisalCycleResponse;
import com.tbcpl.workforce.hr.performance.dto.response.HrKraTemplateResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HrAppraisalCycleService {

    HrAppraisalCycleResponse createCycle(HrAppraisalCycleRequest request, String createdBy);

    HrAppraisalCycleResponse getCycleById(Long id);

    Page<HrAppraisalCycleResponse> getAllCycles(int page, int size);

    Page<HrAppraisalCycleResponse> getCyclesByStatus(String status, int page, int size);

    List<HrAppraisalCycleResponse> getCyclesByYear(Integer year);

    HrAppraisalCycleResponse updateCycle(Long id, HrAppraisalCycleRequest request,
                                         String updatedBy);

    HrAppraisalCycleResponse updateCycleStatus(Long id, String status, String updatedBy);

    void deleteCycle(Long id);

    // ── KRA Templates ─────────────────────────────────────────────────────────

    HrKraTemplateResponse createKraTemplate(HrKraTemplateRequest request, String createdBy);

    HrKraTemplateResponse getKraTemplateById(Long id);

    Page<HrKraTemplateResponse> getAllKraTemplates(int page, int size);

    List<HrKraTemplateResponse> getKraTemplatesByDesignation(String designation);

    HrKraTemplateResponse updateKraTemplate(Long id, HrKraTemplateRequest request,
                                            String updatedBy);

    void deleteKraTemplate(Long id);
}