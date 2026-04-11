package com.tbcpl.workforce.operation.finalreport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tbcpl.workforce.admin.entity.Client;
import com.tbcpl.workforce.admin.repository.ClientRepository;
import com.tbcpl.workforce.common.exception.BusinessException;

import com.tbcpl.workforce.common.util.S3Service;
import com.tbcpl.workforce.operation.cases.entity.Case;
import com.tbcpl.workforce.operation.cases.repository.CaseRepository;
import com.tbcpl.workforce.operation.finalreport.dto.request.CreateFinalReportRequest;
import com.tbcpl.workforce.operation.finalreport.dto.request.FinalReportStatusUpdateRequest;
import com.tbcpl.workforce.operation.finalreport.dto.request.UpdateFinalReportRequest;
import com.tbcpl.workforce.operation.finalreport.dto.response.CaseReportPrefillResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.FinalReportListItemResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.FinalReportResponse;
import com.tbcpl.workforce.operation.finalreport.dto.response.ImageUploadResponse;
import com.tbcpl.workforce.operation.finalreport.entity.FinalReport;
import com.tbcpl.workforce.operation.finalreport.entity.enums.FinalReportStatus;
import com.tbcpl.workforce.operation.finalreport.entity.json.SectionData;
import com.tbcpl.workforce.operation.finalreport.repository.FinalReportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FinalReportServiceImpl implements FinalReportService {


    private final FinalReportRepository finalReportRepository;
    private final CaseRepository        caseRepository;
    private final ClientRepository      clientRepository;
    private final S3Service s3Service;
    private final ObjectMapper          objectMapper;

    public FinalReportServiceImpl(
            FinalReportRepository finalReportRepository,
            CaseRepository caseRepository,
            ClientRepository clientRepository,
            S3Service s3Service,
            ObjectMapper objectMapper
    ) {
        this.finalReportRepository = finalReportRepository;
        this.caseRepository        = caseRepository;
        this.clientRepository      = clientRepository;
        this.s3Service     = s3Service;
        this.objectMapper          = objectMapper;
    }

    // ─────────────────────────────────────────────────────────────────
    // PREFILL
    // ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public CaseReportPrefillResponse getCaseReportPrefill(Long caseId) {
        log.info("Fetching report prefill for caseId: {}", caseId);

        Case caseEntity = findActiveCase(caseId);

        Client client = clientRepository.findActiveClientById(caseEntity.getClientId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Client not found for ID: " + caseEntity.getClientId()));

        boolean reportExists  = finalReportRepository.existsByCaseIdAndIsDeletedFalse(caseId);
        Long existingReportId = null;

        if (reportExists) {
            existingReportId = finalReportRepository
                    .findByCaseIdAndIsDeletedFalse(caseId)
                    .map(FinalReport::getId)
                    .orElse(null);
        }

        return CaseReportPrefillResponse.builder()
                .caseId(caseEntity.getId())
                .caseNumber(caseEntity.getCaseNumber())
                .clientId(client.getClientId())
                .clientName(client.getClientName())
                .clientLogoUrl(client.getLogoUrl())
                .reportAlreadyExists(reportExists)
                .existingReportId(existingReportId)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────
    // IMAGE UPLOAD
    // ─────────────────────────────────────────────────────────────────

    @Override
    public ImageUploadResponse uploadSectionImages(Long caseId, MultipartFile[] files) {
        log.info("Uploading {} section images for caseId: {}", files.length, caseId);

        Case caseEntity = findActiveCase(caseId);

        List<ImageUploadResponse.UploadedImage> results = new ArrayList<>();
        int successCount = 0;
        int failedCount  = 0;

        for (int i = 0; i < files.length; i++) {
            MultipartFile file         = files[i];
            String        originalName = file.getOriginalFilename();

            try {
                // ── Validate content type only ────────────────────────────
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    results.add(buildFailedImage(i, originalName, "Only image files are allowed"));
                    failedCount++;
                    continue;
                }

                // ── Upload to Cloudinary ──────────────────────────────────
                Map<String, String> uploaded = s3Service.uploadFile(
                        file,
                        "finalreports/" + caseEntity.getCaseNumber() + "/sections"
                );

                results.add(ImageUploadResponse.UploadedImage.builder()
                        .index(i)
                        .originalName(originalName)
                        .url(uploaded.get("url"))
                        .publicId(uploaded.get("key"))
                        .success(true)
                        .build());

                successCount++;
                log.info("Image '{}' uploaded for case {}", originalName, caseId);

            } catch (IOException e) {
                log.error("Failed to upload image '{}': {}", originalName, e.getMessage());
                results.add(buildFailedImage(i, originalName, "Upload failed: " + e.getMessage()));
                failedCount++;
            }
        }

        return ImageUploadResponse.builder()
                .images(results)
                .successCount(successCount)
                .failedCount(failedCount)
                .build();
    }


    // ─────────────────────────────────────────────────────────────────
    // CREATE
    // ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FinalReportResponse createReport(CreateFinalReportRequest request, String createdBy) {
        log.info("Creating final report for caseId: {} by: {}", request.getCaseId(), createdBy);

        Case caseEntity = findActiveCase(request.getCaseId());

        if (finalReportRepository.existsByCaseIdAndIsDeletedFalse(request.getCaseId())) {
            throw new BusinessException(
                    "A final report already exists for case: " + caseEntity.getCaseNumber());
        }

        String clientLogoUrl = clientRepository
                .findActiveClientById(caseEntity.getClientId())
                .map(Client::getLogoUrl)
                .orElse(null);

        String reportNumber = generateReportNumber();

        FinalReport report = FinalReport.builder()
                .reportNumber(reportNumber)
                .caseId(caseEntity.getId())
                .caseNumber(caseEntity.getCaseNumber())
                .clientId(caseEntity.getClientId())
                .clientName(caseEntity.getClientName())
                .clientLogoUrl(clientLogoUrl)
                .reportTitle(request.getReportTitle())
                .reportSubtitle(request.getReportSubtitle())
                .preparedFor(request.getPreparedFor())
                .preparedBy(request.getPreparedBy())
                .reportDate(request.getReportDate())
                .sectionsJson(serializeToJson(request.getSections()))
                .tableOfContentsJson(serializeToJson(request.getTableOfContents()))
                .photographicEvidenceJson(serializeToJson(request.getPhotographicEvidence()))
                .reportStatus(FinalReportStatus.DRAFT)
                .isDeleted(false)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        FinalReport saved = finalReportRepository.save(report);
        log.info("Final report created: {} for case: {}", reportNumber, caseEntity.getCaseNumber());

        return mapToResponse(saved);
    }

    // ─────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public FinalReportResponse getReportById(Long reportId) {
        return mapToResponse(findActiveById(reportId));
    }

    @Override
    @Transactional(readOnly = true)
    public FinalReportResponse getReportByCaseId(Long caseId) {
        FinalReport report = finalReportRepository.findByCaseIdAndIsDeletedFalse(caseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No final report found for case ID: " + caseId));
        return mapToResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinalReportListItemResponse> getAllReports(Pageable pageable) {
        return finalReportRepository.findAllActive(pageable)
                .map(this::mapToListItemResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinalReportListItemResponse> getReportsByStatus(
            FinalReportStatus status, Pageable pageable) {
        return finalReportRepository.findByStatus(status, pageable)
                .map(this::mapToListItemResponse);
    }

    // ─────────────────────────────────────────────────────────────────
    // UPDATE
    // ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FinalReportResponse updateReport(
            Long reportId,
            UpdateFinalReportRequest request,
            String updatedBy,
            boolean isAdminEdit
    ) {
        log.info("Updating final report ID: {} by: {} (adminEdit: {})", reportId, updatedBy, isAdminEdit);

        FinalReport report = findActiveById(reportId);

        boolean canEditNormally =
                report.getReportStatus() == FinalReportStatus.DRAFT
                        || report.getReportStatus() == FinalReportStatus.REQUEST_CHANGES
                        || report.getReportStatus() == FinalReportStatus.WAITING_FOR_APPROVAL;

        // ✅ Only admin-edit can bypass APPROVED restriction
        if (!canEditNormally && !isAdminEdit) {
            throw new BusinessException(
                    "Report can only be edited when DRAFT or REQUEST_CHANGES. Current: " + report.getReportStatus());
        }

        report.setReportTitle(request.getReportTitle());
        report.setReportSubtitle(request.getReportSubtitle());
        report.setPreparedFor(request.getPreparedFor());
        report.setPreparedBy(request.getPreparedBy());
        report.setReportDate(request.getReportDate());
        report.setSectionsJson(serializeToJson(request.getSections()));
        report.setTableOfContentsJson(serializeToJson(request.getTableOfContents()));
        report.setPhotographicEvidenceJson(serializeToJson(request.getPhotographicEvidence()));
        report.setUpdatedBy(updatedBy);
        report.setUpdatedAt(LocalDateTime.now());

        // Optional: track that an approved report was modified
        // (only if you have these fields; ignore otherwise)
        // if (isAdminEdit && report.getReportStatus() == FinalReportStatus.APPROVED) { ... }

        FinalReport saved = finalReportRepository.save(report);
        return mapToResponse(saved);
    }


    // ─────────────────────────────────────────────────────────────────
    // STATUS TRANSITIONS
    // ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public FinalReportResponse submitForApproval(Long reportId, String updatedBy) {
        log.info("Report ID: {} submitted for approval by: {}", reportId, updatedBy);

        FinalReport report = findActiveById(reportId);

        if (report.getReportStatus() != FinalReportStatus.DRAFT &&
                report.getReportStatus() != FinalReportStatus.REQUEST_CHANGES &&
                report.getReportStatus() != FinalReportStatus.WAITING_FOR_APPROVAL) {
            throw new BusinessException(
                    "Report can only be submitted when DRAFT, REQUEST_CHANGES, or WAITING_FOR_APPROVAL. Current: "
                            + report.getReportStatus()
            );
        }

        report.setReportStatus(FinalReportStatus.WAITING_FOR_APPROVAL);
        report.setChangeComments(null);
        report.setUpdatedBy(updatedBy);
        report.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(finalReportRepository.save(report));
    }

    @Override
    @Transactional
    public FinalReportResponse updateStatus(
            Long reportId, FinalReportStatusUpdateRequest request, String updatedBy) {
        log.info("Admin updating report ID: {} to: {} by: {}",
                reportId, request.getReportStatus(), updatedBy);

        FinalReport report = findActiveById(reportId);

        if (report.getReportStatus() != FinalReportStatus.WAITING_FOR_APPROVAL) {
            throw new BusinessException(
                    "Status can only be changed when WAITING_FOR_APPROVAL. " +
                            "Current: " + report.getReportStatus());
        }

        FinalReportStatus newStatus = request.getReportStatus();
        if (newStatus != FinalReportStatus.REQUEST_CHANGES
                && newStatus != FinalReportStatus.APPROVED) {
            throw new BusinessException(
                    "Admin can only set status to REQUEST_CHANGES or APPROVED");
        }

        report.setReportStatus(newStatus);
        report.setChangeComments(request.getChangeComments());
        report.setUpdatedBy(updatedBy);
        report.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(finalReportRepository.save(report));
    }

    // ─────────────────────────────────────────────────────────────────
    // SOFT DELETE
    // ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        log.info("Soft deleting final report ID: {}", reportId);
        FinalReport report = findActiveById(reportId);
        report.setIsDeleted(true);
        report.setUpdatedAt(LocalDateTime.now());
        finalReportRepository.save(report);
    }

    // ─────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────

    private Case findActiveCase(Long caseId) {
        return caseRepository.findById(caseId)
                .filter(c -> !c.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Case not found: " + caseId));
    }

    private FinalReport findActiveById(Long reportId) {
        return finalReportRepository.findById(reportId)
                .filter(r -> !r.getIsDeleted())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Final report not found: " + reportId));
    }

    private String generateReportNumber() {
        int  year  = LocalDate.now().getYear();
        Long count = finalReportRepository.countByYear(year);
        return String.format("RPT-%d-%04d", year, (count == null ? 0L : count) + 1);
    }

    private String serializeToJson(Object obj) {
        if (obj == null) return "[]";
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization failed: {}", e.getMessage());
            throw new BusinessException("Failed to process report content");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> deserializeFromJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, List.class);
        } catch (JsonProcessingException e) {
            log.error("JSON deserialization failed: {}", e.getMessage());
            return List.of();
        }
    }

    private List<SectionData> deserializeSections(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, SectionData.class));
        } catch (JsonProcessingException e) {
            log.error("JSON deserialization failed: {}", e.getMessage());
            return List.of();
        }
    }
    private boolean isPreviewEnabled(FinalReportStatus status) {
        return status == FinalReportStatus.WAITING_FOR_APPROVAL
                || status == FinalReportStatus.REQUEST_CHANGES
                || status == FinalReportStatus.APPROVED;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> deserializePhotographicEvidence(String json) {
        if (json == null || json.isBlank() || json.equals("null")) return null;
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize photographicEvidence: {}", e.getMessage());
            return null;
        }
    }

    private ImageUploadResponse.UploadedImage buildFailedImage(
            int index, String name, String error) {
        return ImageUploadResponse.UploadedImage.builder()
                .index(index)
                .originalName(name)
                .success(false)
                .error(error)
                .build();
    }

    private FinalReportResponse mapToResponse(FinalReport r) {
        return FinalReportResponse.builder()
                .id(r.getId())
                .reportNumber(r.getReportNumber())
                .caseId(r.getCaseId())
                .caseNumber(r.getCaseNumber())
                .clientId(r.getClientId())
                .clientName(r.getClientName())
                .clientLogoUrl(r.getClientLogoUrl())
                .reportTitle(r.getReportTitle())
                .reportSubtitle(r.getReportSubtitle())
                .preparedFor(r.getPreparedFor())
                .preparedBy(r.getPreparedBy())
                .reportDate(r.getReportDate())
                .sections(new ArrayList<>(deserializeSections(r.getSectionsJson())))
                .tableOfContents(deserializeFromJson(r.getTableOfContentsJson())
                        .stream().map(Object::toString).toList())
                .photographicEvidence(deserializePhotographicEvidence(r.getPhotographicEvidenceJson()))
                .reportStatus(r.getReportStatus())
                .changeComments(r.getChangeComments())
                .previewEnabled(isPreviewEnabled(r.getReportStatus()))
                .sendReportEnabled(r.getReportStatus() == FinalReportStatus.APPROVED)
                .generatePdfEnabled(r.getReportStatus() == FinalReportStatus.APPROVED)
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }

    private FinalReportListItemResponse mapToListItemResponse(FinalReport r) {
        return FinalReportListItemResponse.builder()
                .id(r.getId())
                .reportNumber(r.getReportNumber())
                .caseId(r.getCaseId())
                .caseNumber(r.getCaseNumber())
                .clientName(r.getClientName())
                .clientLogoUrl(r.getClientLogoUrl())
                .reportTitle(r.getReportTitle())
                .reportDate(r.getReportDate())
                .reportStatus(r.getReportStatus())
                .createdBy(r.getCreatedBy())
                .updatedBy(r.getUpdatedBy())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}
