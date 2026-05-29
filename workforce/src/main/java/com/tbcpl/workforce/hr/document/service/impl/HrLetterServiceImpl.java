package com.tbcpl.workforce.hr.document.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.document.dto.request.HrLetterRecordRequest;
import com.tbcpl.workforce.hr.document.dto.response.HrLetterRecordResponse;
import com.tbcpl.workforce.hr.document.entity.HrLetterRecord;
import com.tbcpl.workforce.hr.document.entity.enums.LetterType;
import com.tbcpl.workforce.hr.document.repository.HrLetterRecordRepository;
import com.tbcpl.workforce.hr.document.service.HrLetterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrLetterServiceImpl implements HrLetterService {

    private final HrLetterRecordRepository    letterRepository;
    private final EmployeeRepository          employeeRepository;
    private final EmployeeNameResolverService  nameResolver;

    @Override
    @Transactional
    public HrLetterRecordResponse issueLetter(HrLetterRecordRequest request, String issuedBy) {
        log.info("Issuing letter type:{} for empId:{} by:{}",
                request.getLetterType(), request.getEmpId(), issuedBy);

        if (!employeeRepository.existsByEmpId(request.getEmpId())) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + request.getEmpId());
        }

        String refNumber = generateReferenceNumber(request.getLetterType());

        HrLetterRecord letter = HrLetterRecord.builder()
                .referenceNumber(refNumber)
                .empId(request.getEmpId().trim())
                .letterType(request.getLetterType())
                .subject(request.getSubject().trim())
                .content(request.getContent())
                .fileUrl(request.getFileUrl())
                .issuedDate(request.getIssuedDate())
                .issuedBy(issuedBy)
                .remarks(request.getRemarks())
                .isAcknowledged(false)
                .isActive(true)
                .createdBy(issuedBy)
                .build();

        HrLetterRecord saved = letterRepository.save(letter);
        log.info("Letter issued with ref:{} ID:{}", refNumber, saved.getId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrLetterRecordResponse getLetterById(Long id) {
        HrLetterRecord letter = findById(id);
        return mapToResponse(letter, resolveCreatedBy(letter.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrLetterRecordResponse getLetterByReferenceNumber(String referenceNumber) {
        HrLetterRecord letter = letterRepository
                .findByReferenceNumberAndIsActiveTrue(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Letter not found with reference number: " + referenceNumber));
        return mapToResponse(letter, resolveCreatedBy(letter.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrLetterRecordResponse> getLettersByEmpId(String empId) {
        log.info("Fetching all letters for empId:{}", empId);
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
        }
        List<HrLetterRecord> letters =
                letterRepository.findByEmpIdAndIsActiveTrueOrderByIssuedDateDesc(empId);
        Map<String, String> nameMap = batchResolve(
                letters.stream().map(HrLetterRecord::getCreatedBy).collect(Collectors.toSet()));
        return letters.stream()
                .map(l -> mapToResponse(l, nameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrLetterRecordResponse> getAllLetters(int page, int size) {
        return letterRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(l -> mapToResponse(l, resolveCreatedBy(l.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrLetterRecordResponse> getLettersByType(String letterType,
                                                         int page, int size) {
        LetterType type = LetterType.valueOf(letterType.toUpperCase());
        return letterRepository
                .findByLetterTypeAndIsActiveTrueOrderByCreatedAtDesc(
                        type, PageRequest.of(page, size))
                .map(l -> mapToResponse(l, resolveCreatedBy(l.getCreatedBy())));
    }

    @Override
    @Transactional
    public HrLetterRecordResponse updateLetter(Long id, HrLetterRecordRequest request,
                                               String updatedBy) {
        log.info("Updating letter ID:{} by:{}", id, updatedBy);
        HrLetterRecord letter = findById(id);

        if (Boolean.TRUE.equals(letter.getIsAcknowledged())) {
            throw new IllegalStateException(
                    "Cannot update a letter that has already been acknowledged");
        }

        if (request.getSubject()    != null) letter.setSubject(request.getSubject().trim());
        if (request.getContent()    != null) letter.setContent(request.getContent());
        if (request.getFileUrl()    != null) letter.setFileUrl(request.getFileUrl());
        if (request.getIssuedDate() != null) letter.setIssuedDate(request.getIssuedDate());
        if (request.getRemarks()    != null) letter.setRemarks(request.getRemarks());
        letter.setCreatedBy(updatedBy);

        HrLetterRecord saved = letterRepository.save(letter);
        log.info("Letter ID:{} updated", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrLetterRecordResponse acknowledgeLetter(Long id, String acknowledgedBy) {
        log.info("Acknowledging letter ID:{} by:{}", id, acknowledgedBy);
        HrLetterRecord letter = findById(id);

        if (Boolean.TRUE.equals(letter.getIsAcknowledged())) {
            throw new IllegalStateException("Letter is already acknowledged");
        }

        letter.setIsAcknowledged(true);
        letter.setAcknowledgedAt(LocalDateTime.now());
        letter.setCreatedBy(acknowledgedBy);

        HrLetterRecord saved = letterRepository.save(letter);
        log.info("Letter ID:{} acknowledged by:{}", id, acknowledgedBy);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteLetter(Long id) {
        log.info("Soft deleting letter ID:{}", id);
        HrLetterRecord letter = findById(id);
        letter.setIsActive(false);
        letterRepository.save(letter);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrLetterRecord findById(Long id) {
        return letterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Letter record not found with ID: " + id));
    }

    /**
     * Generates reference number: TBCPL/{TYPE_CODE}/{YEAR}/{SEQ}
     * e.g. TBCPL/OL/2026/001, TBCPL/IL/2026/004
     */
    private String generateReferenceNumber(LetterType type) {
        String typeCode = getTypeCode(type);
        String year     = String.valueOf(Year.now().getValue());
        String prefix   = "TBCPL/" + typeCode + "/" + year + "/";

        List<String> existing = letterRepository.findLastReferenceByPrefix(prefix);
        int nextSeq = 1;
        if (!existing.isEmpty()) {
            String last = existing.get(0);
            try {
                nextSeq = Integer.parseInt(last.substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return prefix + String.format("%03d", nextSeq);
    }

    private String getTypeCode(LetterType type) {
        return switch (type) {
            case OFFER_LETTER           -> "OL";
            case APPOINTMENT_LETTER     -> "AL";
            case INCREMENT_LETTER       -> "IL";
            case EXPERIENCE_LETTER      -> "EL";
            case RELIEVING_LETTER       -> "RL";
            case NOC_LETTER             -> "NOC";
            case WARNING_LETTER         -> "WL";
            case SHOW_CAUSE_NOTICE      -> "SCN";
            case SALARY_REVISION_LETTER -> "SRL";
            case TRANSFER_LETTER        -> "TL";
            case PROMOTION_LETTER       -> "PL";
            default                     -> "LTR";
        };
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

    private HrLetterRecordResponse mapToResponse(HrLetterRecord l,
                                                 Map<String, String> nameMap) {
        String raw = l.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HrLetterRecordResponse.builder()
                .id(l.getId())
                .referenceNumber(l.getReferenceNumber())
                .empId(l.getEmpId())
                .letterType(l.getLetterType())
                .subject(l.getSubject())
                .content(l.getContent())
                .fileUrl(l.getFileUrl())
                .issuedDate(l.getIssuedDate())
                .issuedBy(l.getIssuedBy())
                .isAcknowledged(l.getIsAcknowledged())
                .acknowledgedAt(l.getAcknowledgedAt())
                .remarks(l.getRemarks())
                .isActive(l.getIsActive())
                .createdAt(l.getCreatedAt())
                .updatedAt(l.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}