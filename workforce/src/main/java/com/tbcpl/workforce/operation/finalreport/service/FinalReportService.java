package com.tbcpl.workforce.operation.finalreport.service;

import com.tbcpl.workforce.operation.finalreport.dto.request.CreateFinalReportRequest;
import com.tbcpl.workforce.operation.finalreport.dto.request.FinalReportStatusUpdateRequest;
import com.tbcpl.workforce.operation.finalreport.dto.request.UpdateFinalReportRequest;
import com.tbcpl.workforce.operation.finalreport.dto.response.CaseReportPrefillResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.FinalReportListItemResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.FinalReportResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.ImageUploadResponse;
import com.tbcpl.workforce.operation.finalreport.entity.enums.FinalReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface FinalReportService {

    CaseReportPrefillResponse getCaseReportPrefill(Long caseId);

    ImageUploadResponse uploadSectionImages(Long caseId, MultipartFile[] files);

    FinalReportResponse createReport(CreateFinalReportRequest request, String createdBy);

    FinalReportResponse getReportById(Long reportId);

    FinalReportResponse getReportByCaseId(Long caseId);

    Page<FinalReportListItemResponse> getAllReports(Pageable pageable);

    Page<FinalReportListItemResponse> getReportsByStatus(FinalReportStatus status, Pageable pageable);

    FinalReportResponse updateReport(Long reportId, UpdateFinalReportRequest request, String updatedBy, boolean isAdminEdit);

    FinalReportResponse submitForApproval(Long reportId, String updatedBy);

    FinalReportResponse updateStatus(Long reportId, FinalReportStatusUpdateRequest request, String updatedBy);

    void deleteReport(Long reportId);
}
