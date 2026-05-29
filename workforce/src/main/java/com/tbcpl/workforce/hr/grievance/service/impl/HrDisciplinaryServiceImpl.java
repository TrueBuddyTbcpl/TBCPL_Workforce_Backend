package com.tbcpl.workforce.hr.grievance.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.grievance.dto.request.HrDisciplinaryActionRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrDisciplinaryStatusRequest;
import com.tbcpl.workforce.hr.grievance.dto.response.HrDisciplinaryActionResponse;
import com.tbcpl.workforce.hr.grievance.entity.HrDisciplinaryAction;
import com.tbcpl.workforce.hr.grievance.entity.enums.DisciplinaryStatus;
import com.tbcpl.workforce.hr.grievance.repository.HrDisciplinaryActionRepository;
import com.tbcpl.workforce.hr.grievance.service.HrDisciplinaryService;
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
public class HrDisciplinaryServiceImpl implements HrDisciplinaryService {

    private final HrDisciplinaryActionRepository disciplinaryRepository;
    private final EmployeeRepository             employeeRepository;
    private final EmployeeNameResolverService     nameResolver;

    @Override
    @Transactional
    public HrDisciplinaryActionResponse initiateAction(HrDisciplinaryActionRequest request,
                                                       String initiatedBy) {
        log.info("Initiating disciplinary action empId:{} type:{} by:{}",
                request.getEmpId(), request.getActionType(), initiatedBy);

        if (!employeeRepository.existsByEmpId(request.getEmpId())) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + request.getEmpId());
        }

        String caseRef = generateCaseReference();

        HrDisciplinaryAction action = HrDisciplinaryAction.builder()
                .caseReference(caseRef)
                .empId(request.getEmpId().trim())
                .actionType(request.getActionType())
                .subject(request.getSubject().trim())
                .incidentDescription(request.getIncidentDescription().trim())
                .incidentDate(request.getIncidentDate())
                .relatedGrievanceId(request.getRelatedGrievanceId())
                .noticeIssuedDate(request.getNoticeIssuedDate())
                .noticeDocumentUrl(request.getNoticeDocumentUrl())
                .initiatedBy(initiatedBy)
                .status(DisciplinaryStatus.INITIATED)
                .isActive(true)
                .createdBy(initiatedBy)
                .build();

        HrDisciplinaryAction saved = disciplinaryRepository.save(action);
        log.info("Disciplinary action initiated caseRef:{} ID:{}", caseRef, saved.getId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrDisciplinaryActionResponse getActionById(Long id) {
        HrDisciplinaryAction action = findById(id);
        return mapToResponse(action, resolveCreatedBy(action.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrDisciplinaryActionResponse getActionByCaseReference(String caseReference) {
        HrDisciplinaryAction action = disciplinaryRepository
                .findByCaseReferenceAndIsActiveTrue(caseReference)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Disciplinary action not found with case reference: " + caseReference));
        return mapToResponse(action, resolveCreatedBy(action.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrDisciplinaryActionResponse> getAllActions(int page, int size) {
        return disciplinaryRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(a -> mapToResponse(a, resolveCreatedBy(a.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrDisciplinaryActionResponse> getActionsByEmpId(String empId,
                                                                int page, int size) {
        return disciplinaryRepository
                .findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
                        empId, PageRequest.of(page, size))
                .map(a -> mapToResponse(a, resolveCreatedBy(a.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrDisciplinaryActionResponse> getActionsByStatus(String status,
                                                                 int page, int size) {
        DisciplinaryStatus ds = DisciplinaryStatus.valueOf(status.toUpperCase());
        return disciplinaryRepository
                .findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
                        ds, PageRequest.of(page, size))
                .map(a -> mapToResponse(a, resolveCreatedBy(a.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrDisciplinaryActionResponse> getEmpDisciplinaryHistory(String empId) {
        log.info("Fetching disciplinary history for empId:{}", empId);
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
        }
        List<HrDisciplinaryAction> actions =
                disciplinaryRepository
                        .findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(empId);
        Map<String, String> nameMap = batchResolve(
                actions.stream().map(HrDisciplinaryAction::getCreatedBy)
                        .collect(Collectors.toSet()));
        return actions.stream()
                .map(a -> mapToResponse(a, nameMap))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HrDisciplinaryActionResponse updateAction(Long id,
                                                     HrDisciplinaryActionRequest request,
                                                     String updatedBy) {
        log.info("Updating disciplinary action ID:{} by:{}", id, updatedBy);
        HrDisciplinaryAction action = findById(id);

        if (action.getStatus() == DisciplinaryStatus.CLOSED
                || action.getStatus() == DisciplinaryStatus.WITHDRAWN) {
            throw new IllegalStateException(
                    "Cannot update a case that is " + action.getStatus().name());
        }

        if (request.getSubject()               != null) action.setSubject(request.getSubject().trim());
        if (request.getIncidentDescription()   != null) action.setIncidentDescription(request.getIncidentDescription());
        if (request.getIncidentDate()          != null) action.setIncidentDate(request.getIncidentDate());
        if (request.getNoticeIssuedDate()      != null) action.setNoticeIssuedDate(request.getNoticeIssuedDate());
        if (request.getNoticeDocumentUrl()     != null) action.setNoticeDocumentUrl(request.getNoticeDocumentUrl());
        if (request.getEmployeeResponse()      != null) action.setEmployeeResponse(request.getEmployeeResponse());
        if (request.getEmployeeResponseDate()  != null) action.setEmployeeResponseDate(request.getEmployeeResponseDate());
        if (request.getFinalDecision()         != null) action.setFinalDecision(request.getFinalDecision());
        if (request.getActionEffectiveDate()   != null) action.setActionEffectiveDate(request.getActionEffectiveDate());
        if (request.getActionEndDate()         != null) action.setActionEndDate(request.getActionEndDate());
        if (request.getDeductionAmount()       != null) action.setDeductionAmount(request.getDeductionAmount());
        if (request.getRelatedGrievanceId()    != null) action.setRelatedGrievanceId(request.getRelatedGrievanceId());
        action.setCreatedBy(updatedBy);

        HrDisciplinaryAction saved = disciplinaryRepository.save(action);
        log.info("Disciplinary action ID:{} updated", id);
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrDisciplinaryActionResponse updateActionStatus(Long id,
                                                           HrDisciplinaryStatusRequest request,
                                                           String updatedBy) {
        log.info("Updating disciplinary action ID:{} status to:{} by:{}",
                id, request.getStatus(), updatedBy);
        HrDisciplinaryAction action = findById(id);

        if (action.getStatus() == DisciplinaryStatus.CLOSED
                || action.getStatus() == DisciplinaryStatus.WITHDRAWN) {
            throw new IllegalStateException(
                    "Cannot change status of a " + action.getStatus().name() + " case");
        }

        action.setStatus(request.getStatus());
        if (request.getFinalDecision()        != null) action.setFinalDecision(request.getFinalDecision());
        if (request.getActionEffectiveDate()  != null) action.setActionEffectiveDate(request.getActionEffectiveDate());
        if (request.getActionEndDate()        != null) action.setActionEndDate(request.getActionEndDate());
        if (request.getDeductionAmount()      != null) action.setDeductionAmount(request.getDeductionAmount());
        if (request.getNoticeDocumentUrl()    != null) action.setNoticeDocumentUrl(request.getNoticeDocumentUrl());
        if (request.getNoticeIssuedDate()     != null) action.setNoticeIssuedDate(request.getNoticeIssuedDate());
        if (request.getEmployeeResponse()     != null) action.setEmployeeResponse(request.getEmployeeResponse());
        if (request.getEmployeeResponseDate() != null) action.setEmployeeResponseDate(request.getEmployeeResponseDate());

        // Auto-set status to NOTICE_ISSUED when notice date is added
        if (request.getNoticeIssuedDate() != null
                && action.getStatus() == DisciplinaryStatus.INITIATED) {
            action.setStatus(DisciplinaryStatus.NOTICE_ISSUED);
        }
        action.setCreatedBy(updatedBy);

        HrDisciplinaryAction saved = disciplinaryRepository.save(action);
        log.info("Disciplinary action ID:{} status updated to:{}",
                id, saved.getStatus());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteAction(Long id) {
        log.info("Soft deleting disciplinary action ID:{}", id);
        HrDisciplinaryAction action = findById(id);
        action.setIsActive(false);
        disciplinaryRepository.save(action);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrDisciplinaryAction findById(Long id) {
        return disciplinaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Disciplinary action not found with ID: " + id));
    }

    /**
     * Generates case reference: DISC-{YEAR}-{SEQ}  e.g. DISC-2026-001
     */
    private String generateCaseReference() {
        String year   = String.valueOf(Year.now().getValue());
        String prefix = "DISC-" + year + "-";
        List<String> last = disciplinaryRepository.findLastCaseRefByPrefix(prefix);
        int nextSeq = 1;
        if (!last.isEmpty()) {
            try {
                nextSeq = Integer.parseInt(last.get(0).substring(prefix.length())) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return prefix + String.format("%03d", nextSeq);
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

    private HrDisciplinaryActionResponse mapToResponse(HrDisciplinaryAction a,
                                                       Map<String, String> nameMap) {
        String raw = a.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        long activeCaseCount = disciplinaryRepository.countActiveCasesByEmpId(a.getEmpId());

        return HrDisciplinaryActionResponse.builder()
                .id(a.getId())
                .caseReference(a.getCaseReference())
                .empId(a.getEmpId())
                .actionType(a.getActionType())
                .subject(a.getSubject())
                .incidentDescription(a.getIncidentDescription())
                .incidentDate(a.getIncidentDate())
                .relatedGrievanceId(a.getRelatedGrievanceId())
                .noticeIssuedDate(a.getNoticeIssuedDate())
                .noticeDocumentUrl(a.getNoticeDocumentUrl())
                .employeeResponse(a.getEmployeeResponse())
                .employeeResponseDate(a.getEmployeeResponseDate())
                .finalDecision(a.getFinalDecision())
                .actionEffectiveDate(a.getActionEffectiveDate())
                .actionEndDate(a.getActionEndDate())
                .deductionAmount(a.getDeductionAmount())
                .initiatedBy(a.getInitiatedBy())
                .status(a.getStatus())
                .isActive(a.getIsActive())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .createdBy(resolvedName)
                .activeCaseCount(activeCaseCount)
                .build();
    }
}