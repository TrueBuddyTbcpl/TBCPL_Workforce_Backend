package com.tbcpl.workforce.hr.document.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.document.dto.request.HrDocumentVerificationRequest;
import com.tbcpl.workforce.hr.document.dto.request.HrEmployeeDocumentRequest;
import com.tbcpl.workforce.hr.document.dto.response.HrEmployeeDocumentResponse;
import com.tbcpl.workforce.hr.document.entity.HrEmployeeDocument;
import com.tbcpl.workforce.hr.document.entity.enums.DocumentStatus;
import com.tbcpl.workforce.hr.document.repository.HrEmployeeDocumentRepository;
import com.tbcpl.workforce.hr.document.service.HrDocumentService;
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
public class HrDocumentServiceImpl implements HrDocumentService {

    private final HrEmployeeDocumentRepository documentRepository;
    private final EmployeeRepository            employeeRepository;
    private final EmployeeNameResolverService    nameResolver;

    @Override
    @Transactional
    public HrEmployeeDocumentResponse uploadDocument(HrEmployeeDocumentRequest request,
                                                     String createdBy) {
        log.info("Uploading document type:{} for empId:{}", request.getDocumentType(),
                request.getEmpId());

        validateEmployeeExists(request.getEmpId());

        // Soft-deactivate any previous document of same type for same employee
        documentRepository
                .findByEmpIdAndDocumentTypeAndIsActiveTrue(
                        request.getEmpId(), request.getDocumentType())
                .ifPresent(existing -> {
                    existing.setIsActive(false);
                    documentRepository.save(existing);
                    log.info("Deactivated previous document ID:{} type:{} for empId:{}",
                            existing.getId(), existing.getDocumentType(), request.getEmpId());
                });

        HrEmployeeDocument document = HrEmployeeDocument.builder()
                .empId(request.getEmpId().trim())
                .documentType(request.getDocumentType())
                .documentName(request.getDocumentName().trim())
                .fileUrl(request.getFileUrl().trim())
                .originalFileName(request.getOriginalFileName())
                .fileMimeType(request.getFileMimeType())
                .fileSizeKb(request.getFileSizeKb())
                .documentNumber(request.getDocumentNumber())
                .issueDate(request.getIssueDate())
                .expiryDate(request.getExpiryDate())
                .isMandatory(Boolean.TRUE.equals(request.getIsMandatory()))
                .status(DocumentStatus.PENDING_VERIFICATION)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrEmployeeDocument saved = documentRepository.save(document);
        log.info("Document uploaded with ID:{} for empId:{}", saved.getId(), request.getEmpId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrEmployeeDocumentResponse getDocumentById(Long id) {
        HrEmployeeDocument doc = findById(id);
        return mapToResponse(doc, resolveCreatedBy(doc.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrEmployeeDocumentResponse> getDocumentsByEmpId(String empId) {
        log.info("Fetching all documents for empId:{}", empId);
        validateEmployeeExists(empId);
        List<HrEmployeeDocument> docs =
                documentRepository.findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(empId);
        Map<String, String> nameMap = batchResolve(
                docs.stream().map(HrEmployeeDocument::getCreatedBy).collect(Collectors.toSet()));
        return docs.stream()
                .map(d -> mapToResponse(d, nameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrEmployeeDocumentResponse> getPendingDocuments(int page, int size) {
        log.info("Fetching PENDING_VERIFICATION documents page:{} size:{}", page, size);
        return documentRepository
                .findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
                        DocumentStatus.PENDING_VERIFICATION,
                        PageRequest.of(page, size, Sort.by("createdAt").ascending()))
                .map(d -> mapToResponse(d, resolveCreatedBy(d.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrEmployeeDocumentResponse> getAllDocuments(int page, int size) {
        return documentRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(d -> mapToResponse(d, resolveCreatedBy(d.getCreatedBy())));
    }

    @Override
    @Transactional
    public HrEmployeeDocumentResponse verifyDocument(Long id,
                                                     HrDocumentVerificationRequest request,
                                                     String verifiedBy) {
        log.info("Verifying document ID:{} status:{} by:{}", id, request.getStatus(), verifiedBy);
        HrEmployeeDocument doc = findById(id);

        if (doc.getStatus() == DocumentStatus.VERIFIED) {
            throw new IllegalStateException("Document is already VERIFIED");
        }

        doc.setStatus(request.getStatus());
        doc.setVerificationRemarks(request.getVerificationRemarks());
        doc.setVerifiedBy(verifiedBy);
        doc.setVerifiedAt(LocalDateTime.now());

        HrEmployeeDocument saved = documentRepository.save(doc);
        log.info("Document ID:{} status updated to:{}", id, request.getStatus());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrEmployeeDocumentResponse requestReUpload(Long id, String remarks,
                                                      String requestedBy) {
        log.info("Requesting re-upload for document ID:{} by:{}", id, requestedBy);
        HrEmployeeDocument doc = findById(id);

        doc.setStatus(DocumentStatus.RE_UPLOAD_REQUESTED);
        doc.setVerificationRemarks(remarks);
        doc.setVerifiedBy(requestedBy);
        doc.setVerifiedAt(LocalDateTime.now());

        HrEmployeeDocument saved = documentRepository.save(doc);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrEmployeeDocumentResponse replaceDocument(Long id,
                                                      HrEmployeeDocumentRequest request,
                                                      String updatedBy) {
        log.info("Replacing document ID:{} for empId:{}", id, request.getEmpId());
        HrEmployeeDocument doc = findById(id);

        doc.setFileUrl(request.getFileUrl().trim());
        if (request.getOriginalFileName() != null)
            doc.setOriginalFileName(request.getOriginalFileName());
        if (request.getFileMimeType() != null)
            doc.setFileMimeType(request.getFileMimeType());
        if (request.getFileSizeKb() != null)
            doc.setFileSizeKb(request.getFileSizeKb());
        if (request.getDocumentNumber() != null)
            doc.setDocumentNumber(request.getDocumentNumber());
        if (request.getIssueDate()  != null) doc.setIssueDate(request.getIssueDate());
        if (request.getExpiryDate() != null) doc.setExpiryDate(request.getExpiryDate());

        // Reset to pending verification on replace
        doc.setStatus(DocumentStatus.PENDING_VERIFICATION);
        doc.setVerifiedBy(null);
        doc.setVerifiedAt(null);
        doc.setVerificationRemarks(null);
        doc.setCreatedBy(updatedBy);

        HrEmployeeDocument saved = documentRepository.save(doc);
        log.info("Document ID:{} replaced and reset to PENDING_VERIFICATION", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteDocument(Long id) {
        log.info("Soft deleting document ID:{}", id);
        HrEmployeeDocument doc = findById(id);
        doc.setIsActive(false);
        documentRepository.save(doc);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrEmployeeDocumentResponse> getDocumentsByEmpId(String empId,
                                                                int page, int size) {
        log.info("Fetching documents for empId:{}", empId);
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
        }
        return documentRepository
                .findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
                        empId, PageRequest.of(page, size))
                .map(d -> mapToResponse(d, resolveCreatedBy(d.getCreatedBy())));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrEmployeeDocument findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with ID: " + id));
    }

    private void validateEmployeeExists(String empId) {
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
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

    private HrEmployeeDocumentResponse mapToResponse(HrEmployeeDocument d,
                                                     Map<String, String> nameMap) {
        String raw = d.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HrEmployeeDocumentResponse.builder()
                .id(d.getId())
                .empId(d.getEmpId())
                .documentType(d.getDocumentType())
                .documentName(d.getDocumentName())
                .fileUrl(d.getFileUrl())
                .originalFileName(d.getOriginalFileName())
                .fileMimeType(d.getFileMimeType())
                .fileSizeKb(d.getFileSizeKb())
                .documentNumber(d.getDocumentNumber())
                .issueDate(d.getIssueDate())
                .expiryDate(d.getExpiryDate())
                .status(d.getStatus())
                .verificationRemarks(d.getVerificationRemarks())
                .verifiedBy(d.getVerifiedBy())
                .verifiedAt(d.getVerifiedAt())
                .isMandatory(d.getIsMandatory())
                .isActive(d.getIsActive())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}