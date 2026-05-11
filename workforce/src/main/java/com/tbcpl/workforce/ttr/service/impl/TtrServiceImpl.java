package com.tbcpl.workforce.ttr.service.impl;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.exception.UnauthorizedAccessException;
import com.tbcpl.workforce.common.util.S3Service;
import com.tbcpl.workforce.operation.cases.repository.CaseRepository;
import com.tbcpl.workforce.operation.finalreport.repository.FinalReportRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportRepository;
import com.tbcpl.workforce.ttr.dto.request.TtrChildCreateRequest;
import com.tbcpl.workforce.ttr.dto.request.TtrCreateRequest;
import com.tbcpl.workforce.ttr.dto.request.TtrStatusUpdateRequest;
import com.tbcpl.workforce.ttr.dto.response.TtrCompletionRecordResponse;
import com.tbcpl.workforce.ttr.dto.response.TtrDashboardResponse;
import com.tbcpl.workforce.ttr.dto.response.TtrResponse;
import com.tbcpl.workforce.ttr.dto.response.TtrStatusHistoryResponse;
import com.tbcpl.workforce.ttr.entity.Ttr;
import com.tbcpl.workforce.ttr.entity.TtrCompletionRecord;
import com.tbcpl.workforce.ttr.entity.TtrStatusHistory;
import com.tbcpl.workforce.ttr.entity.enums.TtrModuleType;
import com.tbcpl.workforce.ttr.entity.enums.TtrStatus;
import com.tbcpl.workforce.ttr.entity.enums.TtrType;
import com.tbcpl.workforce.ttr.repository.TtrCompletionRecordRepository;
import com.tbcpl.workforce.ttr.repository.TtrRepository;
import com.tbcpl.workforce.ttr.repository.TtrStatusHistoryRepository;
import com.tbcpl.workforce.ttr.service.TtrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TtrServiceImpl implements TtrService {

    private final TtrRepository                 ttrRepository;
    private final TtrStatusHistoryRepository    historyRepository;
    private final TtrCompletionRecordRepository completionRecordRepository;
    private final EmployeeRepository            employeeRepository;
    private final PreReportRepository           preReportRepository;
    private final CaseRepository                caseRepository;
    private final FinalReportRepository         finalReportRepository;
    private final S3Service                     s3Service;

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE — Parent TTR
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TtrResponse createParentTtr(TtrCreateRequest request, String createdByEmpId) {
        log.info("Creating Parent TTR type={} by empId={}", request.getTtrType(), createdByEmpId);

        Employee assignedEmployee = findEmployeeByEmpId(request.getAssignedEmpId());
        String linkedItemDisplay  = resolveLinkedItemDisplay(request.getModuleType(), request.getLinkedItemId());
        String ttrNumber          = generateTtrNumber();

        Ttr ttr = Ttr.builder()
                .ttrNumber(ttrNumber)
                .ttrType(request.getTtrType() != null ? request.getTtrType() : TtrType.CUSTOM)
                .departmentId(request.getDepartmentId())
                .departmentName(assignedEmployee.getDepartment().getDepartmentName())
                .assignedEmpId(assignedEmployee.getEmpId())
                .assignedEmpName(assignedEmployee.getFullName())
                .moduleType(request.getModuleType())
                .linkedItemId(request.getLinkedItemId())
                .linkedItemDisplay(linkedItemDisplay)
                .notes(request.getNotes())
                .status(TtrStatus.S1_OPENED)
                .nestingDepth(0)
                .createdBy(createdByEmpId)
                .build();

        Ttr saved = ttrRepository.save(ttr);
        recordHistory(saved, null, TtrStatus.S1_OPENED, createdByEmpId,
                assignedEmployee.getFullName(), "TTR Created", null, null);

        log.info("Parent TTR created: {} [{}]", saved.getTtrNumber(), saved.getTtrType());
        return mapToResponse(saved, true);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CREATE — Child TTR (CUSTOM only)
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TtrResponse createChildTtr(Long parentTtrId, TtrChildCreateRequest request,
                                      String createdByEmpId) {
        log.info("Creating Child TTR under parentId={} by empId={}", parentTtrId, createdByEmpId);

        Ttr parent = findTtrById(parentTtrId);

        if (parent.getTtrType() == TtrType.RECURRING) {
            throw new IllegalArgumentException(
                    "Child TTRs cannot be created under a RECURRING TTR: " + parent.getTtrNumber());
        }
        if (!parent.canHaveChildren()) {
            throw new IllegalArgumentException(
                    "Maximum nesting depth (2) reached. Cannot create further child TTRs under: "
                            + parent.getTtrNumber());
        }
        if (!parent.getAssignedEmpId().equals(createdByEmpId)) {
            throw new UnauthorizedAccessException(
                    "Only the assigned employee of " + parent.getTtrNumber()
                            + " can create child TTRs under it.");
        }

        Employee assignedEmployee = findEmployeeByEmpId(request.getAssignedEmpId());
        String ttrNumber = generateTtrNumber();

        Ttr child = Ttr.builder()
                .ttrNumber(ttrNumber)
                .ttrType(TtrType.CUSTOM)
                .departmentId(parent.getDepartmentId())
                .departmentName(parent.getDepartmentName())
                .assignedEmpId(assignedEmployee.getEmpId())
                .assignedEmpName(assignedEmployee.getFullName())
                .moduleType(parent.getModuleType())
                .linkedItemId(parent.getLinkedItemId())
                .linkedItemDisplay(parent.getLinkedItemDisplay())
                .notes(request.getNotes())
                .status(TtrStatus.S1_OPENED)
                .parentTtr(parent)
                .nestingDepth(parent.getNestingDepth() + 1)
                .createdBy(createdByEmpId)
                .build();

        Ttr saved = ttrRepository.save(child);
        recordHistory(saved, null, TtrStatus.S1_OPENED, createdByEmpId,
                assignedEmployee.getFullName(), "Child TTR Created", null, null);

        log.info("Child TTR created: {} under parent: {}", saved.getTtrNumber(), parent.getTtrNumber());
        return mapToResponse(saved, false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATUS UPDATE — JSON only
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TtrResponse updateStatus(Long ttrId, TtrStatusUpdateRequest request,
                                    String actorEmpId, String actorRole) {
        return doUpdateStatus(ttrId, request, actorEmpId, actorRole);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STATUS UPDATE — With proof file
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public TtrResponse updateStatusWithProof(Long ttrId, TtrStatusUpdateRequest request,
                                             MultipartFile proofFile,
                                             String actorEmpId, String actorRole) {
        if (proofFile != null && !proofFile.isEmpty()) {
            try {
                Map<String, String> result = s3Service.uploadFile(proofFile, "ttr-proofs/" + ttrId);
                request.setProofFileUrl(result.get("url"));
                request.setProofFileName(result.get("file_name"));
                log.info("Proof file uploaded for TTR {}: {}", ttrId, result.get("url"));
            } catch (IOException e) {
                log.error("Proof file upload failed for TTR {}: {}", ttrId, e.getMessage());
                throw new RuntimeException("Proof file upload failed: " + e.getMessage(), e);
            }
        }
        return doUpdateStatus(ttrId, request, actorEmpId, actorRole);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CORE STATUS UPDATE LOGIC
    // ─────────────────────────────────────────────────────────────────────────

    private TtrResponse doUpdateStatus(Long ttrId, TtrStatusUpdateRequest request,
                                       String actorEmpId, String actorRole) {
        Ttr ttr = findTtrById(ttrId);
        TtrStatus currentStatus = ttr.getStatus();
        TtrStatus newStatus     = request.getNewStatus();

        log.info("Status update: TTR={} {} → {} by empId={}",
                ttr.getTtrNumber(), currentStatus, newStatus, actorEmpId);

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Invalid status transition: %s → %s", currentStatus, newStatus));
        }

        validateStatusTransitionActor(ttr, currentStatus, newStatus, actorEmpId, actorRole);

        if (newStatus == TtrStatus.S5_CLOSED && ttr.isParent()) {
            long openChildren = ttrRepository.countOpenChildrenByParentId(ttrId);
            if (openChildren > 0) {
                throw new IllegalStateException(
                        "Cannot close TTR " + ttr.getTtrNumber()
                                + ". Close all " + openChildren + " child TTR(s) first.");
            }
        }

        Employee actor = findEmployeeByEmpId(actorEmpId);

        // ── RECURRING: auto-reset on S3_COMPLETED ────────────────────────────
        if (ttr.getTtrType() == TtrType.RECURRING && newStatus == TtrStatus.S3_COMPLETED) {

            recordHistory(ttr, currentStatus, TtrStatus.S3_COMPLETED,
                    actorEmpId, actor.getFullName(),
                    request.getComments(), request.getProofFileUrl(), request.getProofFileName());

            int nextCycle = completionRecordRepository.findMaxCycleNumberByTtrId(ttr.getId()) + 1;

            TtrCompletionRecord record = TtrCompletionRecord.builder()
                    .ttr(ttr)
                    .cycleNumber(nextCycle)
                    .completedByEmpId(actorEmpId)
                    .completedByName(actor.getFullName())
                    .completedAt(LocalDateTime.now())
                    .proofFileUrl(request.getProofFileUrl())
                    .proofFileName(request.getProofFileName())
                    .notes(request.getComments())
                    .build();

            completionRecordRepository.save(record);

            ttr.setStatus(TtrStatus.S1_OPENED);
            Ttr saved = ttrRepository.save(ttr);

            recordHistory(saved, TtrStatus.S3_COMPLETED, TtrStatus.S1_OPENED,
                    "SYSTEM", "System Auto-Reset",
                    "Recurring task completed (Cycle #" + nextCycle + ") — auto-reset to Opened",
                    null, null);

            log.info("RECURRING TTR {} completed cycle #{} → auto-reset to S1_OPENED",
                    ttr.getTtrNumber(), nextCycle);
            return mapToResponse(saved, false);
        }

        // ── CUSTOM or non-completion transitions ─────────────────────────────
        ttr.setStatus(newStatus);
        Ttr saved = ttrRepository.save(ttr);

        recordHistory(saved, currentStatus, newStatus,
                actorEmpId, actor.getFullName(),
                request.getComments(), request.getProofFileUrl(), request.getProofFileName());

        log.info("TTR {} status updated: {} → {}", ttr.getTtrNumber(), currentStatus, newStatus);
        return mapToResponse(saved, false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // COMPLETION HISTORY
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<TtrCompletionRecordResponse> getCompletionHistory(Long ttrId, Pageable pageable) {
        Ttr ttr = findTtrById(ttrId);
        if (ttr.getTtrType() != TtrType.RECURRING) {
            throw new IllegalArgumentException(
                    "Completion history is only available for RECURRING TTRs. TTR "
                            + ttr.getTtrNumber() + " is of type " + ttr.getTtrType());
        }
        return completionRecordRepository
                .findByTtrIdOrderByCompletedAtDesc(ttrId, pageable)
                .map(this::mapCompletionRecord);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public TtrResponse getTtrById(Long id) {
        return mapToResponse(findTtrById(id), true);
    }

    /**
     * Single unified getAllTtrs — filters by departmentId, status, assignedEmpId, ttrType.
     * Replaces the old multi-branch version entirely.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TtrResponse> getAllTtrs(int page, int size, Long departmentId,
                                        String status, String assignedEmpId,
                                        String ttrType) {

        TtrStatus ttrStatus = (status != null && !status.isBlank())
                ? TtrStatus.valueOf(status)
                : null;

        TtrType type = (ttrType != null && !ttrType.isBlank())
                ? TtrType.valueOf(ttrType)
                : null;

        Page<Ttr> ttrPage = ttrRepository.findAllWithFilters(
                departmentId,
                ttrStatus,
                assignedEmpId,
                type,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        return ttrPage.map(ttr -> mapToResponse(ttr, false)); // ← fixed: was this::toResponse
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TtrResponse> getTtrsByDepartment(Long departmentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ttrRepository.findByIsActiveTrueAndDepartmentId(departmentId, pageable)
                .map(t -> mapToResponse(t, false));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DASHBOARD
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<TtrDashboardResponse> getDashboardMetrics() {
        return List.of(
                buildDepartmentMetrics("Admin"),
                buildDepartmentMetrics("Operation"),
                buildDepartmentMetrics("Accounts"),
                buildDepartmentMetrics("HR")
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TtrDashboardResponse getDepartmentMetrics(Long departmentId) {
        return TtrDashboardResponse.builder()
                .total(ttrRepository.countByDepartmentIdAndIsActiveTrue(departmentId))
                .opened(ttrRepository.countByDepartmentIdAndStatusAndIsActiveTrue(
                        departmentId, TtrStatus.S1_OPENED))
                .inProgress(ttrRepository.countByDepartmentIdAndStatusAndIsActiveTrue(
                        departmentId, TtrStatus.S2_IN_PROGRESS))
                .completed(ttrRepository.countByDepartmentIdAndStatusAndIsActiveTrue(
                        departmentId, TtrStatus.S3_COMPLETED))
                .changesRequested(ttrRepository.countByDepartmentIdAndStatusAndIsActiveTrue(
                        departmentId, TtrStatus.S4_CHANGES_REQUESTED))
                .closed(ttrRepository.countByDepartmentIdAndStatusAndIsActiveTrue(
                        departmentId, TtrStatus.S5_CLOSED))
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private Ttr findTtrById(Long id) {
        return ttrRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TTR not found with id: " + id));
    }

    private Employee findEmployeeByEmpId(String empId) {
        return employeeRepository.findByEmpIdWithDepartmentAndRole(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + empId));
    }

    private String generateTtrNumber() {
        Integer max = ttrRepository.findMaxTtrSequence();
        int next = (max == null ? 0 : max) + 1;
        return String.format("TTR%04d", next);
    }

    private String resolveLinkedItemDisplay(TtrModuleType moduleType, Long linkedItemId) {
        if (moduleType == null || linkedItemId == null) return null;
        return switch (moduleType) {
            case PREREPORT -> preReportRepository.findById(linkedItemId)
                    .map(r -> r.getReportId() + " | " + r.getClient().getClientName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "PreReport not found: " + linkedItemId));
            case CASE -> caseRepository.findById(linkedItemId)
                    .map(c -> c.getCaseNumber() + " | " + c.getClientName())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Case not found: " + linkedItemId));
            case FINAL_REPORT -> finalReportRepository.findById(linkedItemId)
                    .map(f -> f.getReportNumber() + " | " + f.getReportTitle())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "FinalReport not found: " + linkedItemId));
        };
    }

    private void validateStatusTransitionActor(Ttr ttr, TtrStatus current,
                                               TtrStatus next, String actorEmpId,
                                               String actorRole) {
        boolean isAdmin    = actorRole != null &&
                (actorRole.equalsIgnoreCase("ADMIN") ||
                        actorRole.equalsIgnoreCase("SUPER_ADMIN") ||
                        actorRole.equalsIgnoreCase("GLOBAL_ADMIN"));
        boolean isAssigned = ttr.getAssignedEmpId().equals(actorEmpId);
        boolean isCreator  = ttr.getCreatedBy().equals(actorEmpId);

        switch (next) {
            case S2_IN_PROGRESS -> {
                if (!isAssigned)
                    throw new UnauthorizedAccessException(
                            "Only the assigned employee can start this TTR.");
            }
            case S3_COMPLETED -> {
                if (!isAssigned)
                    throw new UnauthorizedAccessException(
                            "Only the assigned employee can mark this TTR as completed.");
            }
            case S4_CHANGES_REQUESTED, S5_CLOSED -> {
                if (ttr.isParent()) {
                    if (!isAdmin)
                        throw new UnauthorizedAccessException(
                                "Only Admin/Super Admin can close or request changes on a Parent TTR.");
                } else {
                    if (!isCreator && !isAdmin)
                        throw new UnauthorizedAccessException(
                                "Only the creator of this Child TTR (or an Admin) can close or request changes.");
                }
            }
            default -> throw new IllegalArgumentException("Unhandled status transition: " + next);
        }
    }

    private void recordHistory(Ttr ttr, TtrStatus oldStatus, TtrStatus newStatus,
                               String changedBy, String changedByName,
                               String comments, String proofFileUrl, String proofFileName) {
        TtrStatusHistory history = TtrStatusHistory.builder()
                .ttr(ttr)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .changedByName(changedByName)
                .comments(comments)
                .proofFileUrl(proofFileUrl)
                .proofFileName(proofFileName)
                .build();
        historyRepository.save(history);
    }

    private TtrDashboardResponse buildDepartmentMetrics(String departmentName) {
        return TtrDashboardResponse.builder()
                .departmentName(departmentName)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAPPERS
    // ─────────────────────────────────────────────────────────────────────────

    public TtrResponse mapToResponse(Ttr ttr, boolean includeChildren) {
        TtrResponse.TtrResponseBuilder builder = TtrResponse.builder()
                .id(ttr.getId())
                .ttrNumber(ttr.getTtrNumber())
                .ttrType(ttr.getTtrType())
                .ttrTypeDisplayName(ttr.getTtrType().getDisplayName())
                .departmentId(ttr.getDepartmentId())
                .departmentName(ttr.getDepartmentName())
                .assignedEmpId(ttr.getAssignedEmpId())
                .assignedEmpName(ttr.getAssignedEmpName())
                .moduleType(ttr.getModuleType())
                .moduleDisplayName(ttr.getModuleType() != null ? ttr.getModuleType().getDisplayName() : null)
                .linkedItemId(ttr.getLinkedItemId())
                .linkedItemDisplay(ttr.getLinkedItemDisplay())
                .notes(ttr.getNotes())
                .status(ttr.getStatus())
                .statusDisplayName(ttr.getStatus().name())
                .nestingDepth(ttr.getNestingDepth())
                .createdBy(ttr.getCreatedBy())
                .createdAt(ttr.getCreatedAt())
                .updatedAt(ttr.getUpdatedAt());

        if (ttr.getParentTtr() != null) {
            builder.parentTtrNumber(ttr.getParentTtr().getTtrNumber());
        }

        if (ttr.getTtrType() == TtrType.RECURRING) {
            builder.totalCompletionCount(
                    (int) completionRecordRepository.countByTtrId(ttr.getId()));
        }

        if (includeChildren) {
            List<Ttr> children = ttrRepository.findChildrenByParentId(ttr.getId());
            builder.children(children.stream()
                    .map(c -> mapToResponse(c, true))
                    .collect(Collectors.toList()));

            builder.statusHistory(historyRepository
                    .findByTtrIdOrderByChangedAtAsc(ttr.getId())
                    .stream()
                    .map(h -> TtrStatusHistoryResponse.builder()
                            .id(h.getId())
                            .oldStatus(h.getOldStatus())
                            .newStatus(h.getNewStatus())
                            .changedBy(h.getChangedBy())
                            .changedByName(h.getChangedByName())
                            .comments(h.getComments())
                            .proofFileUrl(h.getProofFileUrl())
                            .proofFileName(h.getProofFileName())
                            .changedAt(h.getChangedAt())
                            .build())
                    .collect(Collectors.toList()));

            if (ttr.getTtrType() == TtrType.RECURRING) {
                Pageable top20 = PageRequest.of(0, 20, Sort.by("completedAt").descending());
                builder.completionRecords(
                        completionRecordRepository
                                .findByTtrIdOrderByCompletedAtDesc(ttr.getId(), top20)
                                .stream()
                                .map(this::mapCompletionRecord)
                                .collect(Collectors.toList()));
            }
        }

        return builder.build();
    }

    private TtrCompletionRecordResponse mapCompletionRecord(TtrCompletionRecord r) {
        return TtrCompletionRecordResponse.builder()
                .id(r.getId())
                .ttrId(r.getTtr().getId())
                .ttrNumber(r.getTtr().getTtrNumber())
                .cycleNumber(r.getCycleNumber())
                .completedByEmpId(r.getCompletedByEmpId())
                .completedByName(r.getCompletedByName())
                .completedAt(r.getCompletedAt())
                .proofFileUrl(r.getProofFileUrl())
                .proofFileName(r.getProofFileName())
                .notes(r.getNotes())
                .build();
    }
}