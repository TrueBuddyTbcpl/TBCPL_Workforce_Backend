package com.tbcpl.workforce.hr.grievance.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceRemarkRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceUpdateRequest;
import com.tbcpl.workforce.hr.grievance.dto.request.HrGrievanceActionRequest;
import com.tbcpl.workforce.hr.grievance.dto.response.HrGrievanceRemarkResponse;
import com.tbcpl.workforce.hr.grievance.dto.response.HrGrievanceResponse;
import com.tbcpl.workforce.hr.grievance.entity.HrGrievance;
import com.tbcpl.workforce.hr.grievance.entity.HrGrievanceRemark;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievancePriority;
import com.tbcpl.workforce.hr.grievance.entity.enums.GrievanceStatus;
import com.tbcpl.workforce.hr.grievance.repository.HrGrievanceRepository;
import com.tbcpl.workforce.hr.grievance.service.HrGrievanceService;
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
public class HrGrievanceServiceImpl implements HrGrievanceService {

    private final HrGrievanceRepository      grievanceRepository;
    private final EmployeeRepository         employeeRepository;
    private final EmployeeNameResolverService nameResolver;

    @Override
    @Transactional
    public HrGrievanceResponse raiseGrievance(HrGrievanceRequest request, String createdBy) {
        log.info("Raising grievance empId:{} category:{} by:{}",
                request.getEmpId(), request.getCategory(), createdBy);

        if (!employeeRepository.existsByEmpId(request.getEmpId())) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + request.getEmpId());
        }

        String ticket = generateTicketNumber();

        HrGrievance grievance = HrGrievance.builder()
                .ticketNumber(ticket)
                .empId(request.getEmpId().trim())
                .category(request.getCategory())
                .subject(request.getSubject().trim())
                .description(request.getDescription().trim())
                .attachmentUrl(request.getAttachmentUrl())
                .priority(request.getPriority() != null
                        ? request.getPriority() : GrievancePriority.MEDIUM)
                .isAnonymous(Boolean.TRUE.equals(request.getIsAnonymous()))
                .status(GrievanceStatus.SUBMITTED)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrGrievance saved = grievanceRepository.save(grievance);
        log.info("Grievance raised ticket:{} ID:{}", ticket, saved.getId());
        return mapToResponse(saved, false, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrGrievanceResponse getGrievanceById(Long id, boolean includeInternal) {
        HrGrievance grievance = grievanceRepository.findByIdWithRemarks(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grievance not found with ID: " + id));
        return mapToResponse(grievance, includeInternal,
                resolveCreatedBy(grievance.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrGrievanceResponse getGrievanceByTicket(String ticketNumber,
                                                    boolean includeInternal) {
        HrGrievance grievance = grievanceRepository
                .findByTicketNumberAndIsActiveTrue(ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grievance not found with ticket: " + ticketNumber));
        return mapToResponse(grievance, includeInternal,
                resolveCreatedBy(grievance.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrGrievanceResponse> getAllGrievances(int page, int size) {
        return grievanceRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(g -> mapToResponse(g, true, resolveCreatedBy(g.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrGrievanceResponse> getGrievancesByEmpId(String empId, int page, int size) {
        return grievanceRepository
                .findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(
                        empId, PageRequest.of(page, size))
                .map(g -> mapToResponse(g, false, resolveCreatedBy(g.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrGrievanceResponse> getGrievancesByStatus(String status, int page, int size) {
        GrievanceStatus gs = GrievanceStatus.valueOf(status.toUpperCase());
        return grievanceRepository
                .findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
                        gs, PageRequest.of(page, size))
                .map(g -> mapToResponse(g, true, resolveCreatedBy(g.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrGrievanceResponse> getGrievancesByPriority(String priority,
                                                             int page, int size) {
        GrievancePriority gp = GrievancePriority.valueOf(priority.toUpperCase());
        return grievanceRepository
                .findByPriorityAndIsActiveTrueOrderByCreatedAtDesc(
                        gp, PageRequest.of(page, size))
                .map(g -> mapToResponse(g, true, resolveCreatedBy(g.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrGrievanceResponse> getGrievancesAssignedTo(String assignedTo,
                                                             int page, int size) {
        return grievanceRepository
                .findByAssignedToAndIsActiveTrueOrderByCreatedAtDesc(
                        assignedTo, PageRequest.of(page, size))
                .map(g -> mapToResponse(g, true, resolveCreatedBy(g.getCreatedBy())));
    }

    @Override
    @Transactional
    public HrGrievanceResponse updateGrievance(Long id, HrGrievanceUpdateRequest request,
                                               String updatedBy) {
        log.info("Updating grievance ID:{} status:{} by:{}", id, request.getStatus(), updatedBy);
        HrGrievance grievance = findById(id);

        if (grievance.getStatus() == GrievanceStatus.RESOLVED
                || grievance.getStatus() == GrievanceStatus.CLOSED) {
            throw new IllegalStateException(
                    "Cannot update a grievance that is already "
                            + grievance.getStatus().name());
        }

        if (request.getStatus()           != null) grievance.setStatus(request.getStatus());
        if (request.getPriority()         != null) grievance.setPriority(request.getPriority());
        if (request.getResolutionRemarks() != null) grievance.setResolutionRemarks(request.getResolutionRemarks());

        if (request.getAssignedTo() != null
                && !request.getAssignedTo().equals(grievance.getAssignedTo())) {
            grievance.setAssignedTo(request.getAssignedTo());
            grievance.setAssignedAt(LocalDateTime.now());
            // Auto-transition to UNDER_REVIEW on first assignment
            if (grievance.getStatus() == GrievanceStatus.SUBMITTED) {
                grievance.setStatus(GrievanceStatus.UNDER_REVIEW);
            }
        }
        grievance.setCreatedBy(updatedBy);

        HrGrievance saved = grievanceRepository.save(grievance);
        log.info("Grievance ID:{} updated to status:{}", id, saved.getStatus());
        return mapToResponse(saved, true, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrGrievanceResponse addRemark(Long id, HrGrievanceRemarkRequest request,
                                         String remarkedBy) {
        log.info("Adding remark to grievance ID:{} by:{} internal:{}",
                id, remarkedBy, request.getIsInternal());
        HrGrievance grievance = findById(id);

        HrGrievanceRemark remark = HrGrievanceRemark.builder()
                .grievance(grievance)
                .remarkedBy(remarkedBy)
                .remarkedByRole(request.getRemarkedByRole())
                .remark(request.getRemark().trim())
                .isInternal(Boolean.TRUE.equals(request.getIsInternal()))
                .isActive(true)
                .build();

        grievance.getRemarks().add(remark);
        HrGrievance saved = grievanceRepository.save(grievance);
        log.info("Remark added to grievance ID:{}", id);
        return mapToResponse(saved, Boolean.TRUE.equals(request.getIsInternal()),
                resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrGrievanceResponse resolveGrievance(Long id, String resolutionRemarks,
                                                String resolvedBy) {
        log.info("Resolving grievance ID:{} by:{}", id, resolvedBy);
        HrGrievance grievance = findById(id);

        if (grievance.getStatus() == GrievanceStatus.RESOLVED) {
            throw new IllegalStateException("Grievance is already RESOLVED");
        }
        if (grievance.getStatus() == GrievanceStatus.CLOSED) {
            throw new IllegalStateException("Cannot resolve a CLOSED grievance");
        }

        grievance.setStatus(GrievanceStatus.RESOLVED);
        grievance.setResolutionRemarks(resolutionRemarks);
        grievance.setResolvedBy(resolvedBy);
        grievance.setResolvedAt(LocalDateTime.now());
        grievance.setCreatedBy(resolvedBy);

        HrGrievance saved = grievanceRepository.save(grievance);
        log.info("Grievance ID:{} resolved by:{}", id, resolvedBy);
        return mapToResponse(saved, true, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteGrievance(Long id) {
        log.info("Soft deleting grievance ID:{}", id);
        HrGrievance grievance = findById(id);
        grievance.setIsActive(false);
        grievanceRepository.save(grievance);
    }

    @Override
    @Transactional
    public HrGrievanceResponse applyGrievanceAction(Long id,
                                                    HrGrievanceActionRequest request,
                                                    String actionBy) {
        log.info("Apply action on grievance ID:{} status:{} by:{}",
                id, request.getStatus(), actionBy);
        HrGrievance grievance = findById(id);

        if (grievance.getStatus() == GrievanceStatus.RESOLVED
                || grievance.getStatus() == GrievanceStatus.CLOSED) {
            throw new IllegalStateException(
                    "Cannot apply action on a " + grievance.getStatus().name()
                            + " grievance");
        }

        // Assign handler on UNDER_REVIEW transition
        if (request.getStatus() == GrievanceStatus.UNDER_REVIEW
                && request.getAssignedTo() != null) {
            grievance.setAssignedTo(request.getAssignedTo());
            grievance.setAssignedAt(LocalDateTime.now());
        }

        // Escalation: update assignee + status
        if (request.getStatus() == GrievanceStatus.ESCALATED
                && request.getEscalatedTo() != null) {
            grievance.setAssignedTo(request.getEscalatedTo());
            grievance.setAssignedAt(LocalDateTime.now());
        }

        // Closing / Rejecting: must have action remarks
        if ((request.getStatus() == GrievanceStatus.CLOSED
                || request.getStatus() == GrievanceStatus.REJECTED)
                && (request.getActionRemarks() == null
                || request.getActionRemarks().isBlank())) {
            throw new IllegalArgumentException(
                    "Action remarks are required when closing or rejecting a grievance");
        }

        if (request.getActionRemarks() != null) {
            grievance.setResolutionRemarks(request.getActionRemarks());
        }

        grievance.setStatus(request.getStatus());
        grievance.setCreatedBy(actionBy);

        HrGrievance saved = grievanceRepository.save(grievance);
        log.info("Grievance ID:{} action applied → status:{}", id, saved.getStatus());
        return mapToResponse(saved, true, resolveCreatedBy(saved.getCreatedBy()));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrGrievance findById(Long id) {
        return grievanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Grievance not found with ID: " + id));
    }

    /**
     * Generates ticket: GRV-{YEAR}-{SEQ}  e.g. GRV-2026-001
     */
    private String generateTicketNumber() {
        String year   = String.valueOf(Year.now().getValue());
        String prefix = "GRV-" + year + "-";
        List<String> last = grievanceRepository.findLastTicketByPrefix(prefix);
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

    private HrGrievanceResponse mapToResponse(HrGrievance g,
                                              boolean includeInternal,
                                              Map<String, String> nameMap) {
        String raw = g.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        List<HrGrievanceRemarkResponse> remarkResponses =
                (g.getRemarks() == null || g.getRemarks().isEmpty())
                        ? Collections.emptyList()
                        : g.getRemarks().stream()
                        .filter(r -> Boolean.TRUE.equals(r.getIsActive()))
                        .filter(r -> includeInternal || Boolean.FALSE.equals(r.getIsInternal()))
                        .map(this::mapRemarkToResponse)
                        .collect(Collectors.toList());

        return HrGrievanceResponse.builder()
                .id(g.getId())
                .ticketNumber(g.getTicketNumber())
                .empId(Boolean.TRUE.equals(g.getIsAnonymous()) ? "ANONYMOUS" : g.getEmpId())
                .category(g.getCategory())
                .subject(g.getSubject())
                .description(g.getDescription())
                .attachmentUrl(g.getAttachmentUrl())
                .priority(g.getPriority())
                .status(g.getStatus())
                .assignedTo(g.getAssignedTo())
                .assignedAt(g.getAssignedAt())
                .resolutionRemarks(g.getResolutionRemarks())
                .resolvedAt(g.getResolvedAt())
                .resolvedBy(g.getResolvedBy())
                .isAnonymous(g.getIsAnonymous())
                .remarks(remarkResponses)
                .isActive(g.getIsActive())
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }

    private HrGrievanceRemarkResponse mapRemarkToResponse(HrGrievanceRemark r) {
        return HrGrievanceRemarkResponse.builder()
                .id(r.getId())
                .remarkedBy(r.getRemarkedBy())
                .remarkedByRole(r.getRemarkedByRole())
                .remark(r.getRemark())
                .isInternal(r.getIsInternal())
                .createdAt(r.getCreatedAt())
                .build();
    }
}