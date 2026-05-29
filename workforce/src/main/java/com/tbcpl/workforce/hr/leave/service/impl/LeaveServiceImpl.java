package com.tbcpl.workforce.hr.leave.service.impl;

import com.tbcpl.workforce.auth.repository.EmployeeRepository;
import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.leave.dto.request.LeaveActionRequest;
import com.tbcpl.workforce.hr.leave.dto.request.LeaveApplicationRequest;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveApplicationResponse;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveBalanceResponse;
import com.tbcpl.workforce.hr.leave.dto.response.LeaveBalanceSummaryResponse;
import com.tbcpl.workforce.hr.leave.entity.LeaveApplication;
import com.tbcpl.workforce.hr.leave.entity.LeaveBalance;
import com.tbcpl.workforce.hr.attendance.entity.LeaveType;
import com.tbcpl.workforce.hr.attendance.entity.enums.LeaveStatus;
import com.tbcpl.workforce.hr.leave.repository.LeaveApplicationRepository;
import com.tbcpl.workforce.hr.leave.repository.LeaveBalanceRepository;
import com.tbcpl.workforce.hr.attendance.repository.LeaveTypeRepository;
import com.tbcpl.workforce.hr.leave.service.LeaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaveServiceImpl implements LeaveService {

    private final LeaveApplicationRepository  leaveApplicationRepository;
    private final LeaveBalanceRepository      leaveBalanceRepository;
    private final LeaveTypeRepository         leaveTypeRepository;
    private final EmployeeRepository          employeeRepository;
    private final EmployeeNameResolverService  nameResolver;

    // ─────────────────────────────────────────────────────────────────────────
    // LEAVE BALANCE
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LeaveBalanceResponse initializeLeaveBalance(String empId, Long leaveTypeId,
                                                       Integer year, String createdBy) {
        log.info("Initializing leave balance for empId:{} leaveTypeId:{} year:{}",
                empId, leaveTypeId, year);

        validateEmployeeExists(empId);

        LeaveType leaveType = findLeaveTypeById(leaveTypeId);

        if (leaveBalanceRepository.existsByEmpIdAndLeaveTypeIdAndBalanceYear(
                empId, leaveTypeId, year)) {
            throw new DuplicateResourceException(
                    "Leave balance already initialized for empId: " + empId
                            + ", leaveType: " + leaveType.getLeaveTypeName()
                            + ", year: " + year);
        }

        double allocated = leaveType.getMaxDaysPerYear();

        LeaveBalance balance = LeaveBalance.builder()
                .empId(empId)
                .leaveType(leaveType)
                .balanceYear(year)
                .totalAllocated(allocated)
                .totalUsed(0.0)
                .totalPending(0.0)
                .carriedForward(0.0)
                .availableBalance(allocated)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        LeaveBalance saved = leaveBalanceRepository.save(balance);
        log.info("Leave balance initialized with ID: {}", saved.getId());
        return mapBalanceToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveBalanceSummaryResponse getLeaveBalanceSummary(String empId, Integer year) {
        log.info("Fetching leave balance summary for empId:{} year:{}", empId, year);
        validateEmployeeExists(empId);

        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        List<LeaveBalance> balances =
                leaveBalanceRepository.findByEmpIdAndYearWithLeaveType(empId, targetYear);

        long pendingCount = leaveApplicationRepository
                .countByStatusAndIsActiveTrue(LeaveStatus.PENDING);

        return LeaveBalanceSummaryResponse.builder()
                .empId(empId)
                .year(targetYear)
                .balances(balances.stream()
                        .map(this::mapBalanceToResponse)
                        .collect(Collectors.toList()))
                .pendingApplicationsCount(pendingCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> getLeaveBalances(String empId, Integer year) {
        log.info("Fetching leave balances for empId:{} year:{}", empId, year);
        validateEmployeeExists(empId);

        List<LeaveBalance> balances = (year != null)
                ? leaveBalanceRepository.findByEmpIdAndYearWithLeaveType(empId, year)
                : leaveBalanceRepository.findByEmpIdAndIsActiveTrue(empId);

        return balances.stream().map(this::mapBalanceToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void processYearEndCarryForward(Integer year, String processedBy) {
        log.info("Processing year-end carry-forward for year: {} by: {}", year, processedBy);

        List<LeaveBalance> eligibleBalances =
                leaveBalanceRepository.findAllEligibleForCarryForward(year);

        int nextYear    = year + 1;
        int processedCount = 0;

        for (LeaveBalance current : eligibleBalances) {
            LeaveType leaveType = current.getLeaveType();
            double available    = current.getAvailableBalance();
            if (available <= 0) continue;

            double maxCarry = leaveType.getMaxCarryForwardDays() != null
                    ? leaveType.getMaxCarryForwardDays() : 0;
            double carryAmount = Math.min(available, maxCarry);
            if (carryAmount <= 0) continue;

            // Check if next-year balance already exists
            boolean nextYearExists = leaveBalanceRepository
                    .existsByEmpIdAndLeaveTypeIdAndBalanceYear(
                            current.getEmpId(), leaveType.getId(), nextYear);

            if (nextYearExists) {
                // Add carry-forward to existing next-year balance
                LeaveBalance nextYear_balance = leaveBalanceRepository
                        .findByEmpIdAndLeaveTypeIdAndBalanceYear(
                                current.getEmpId(), leaveType.getId(), nextYear)
                        .orElseThrow();
                nextYear_balance.setCarriedForward(
                        nextYear_balance.getCarriedForward() + carryAmount);
                nextYear_balance.recalculateAvailableBalance();
                leaveBalanceRepository.save(nextYear_balance);
            } else {
                // Create new balance for next year with carry-forward
                double allocated = leaveType.getMaxDaysPerYear();
                LeaveBalance newBalance = LeaveBalance.builder()
                        .empId(current.getEmpId())
                        .leaveType(leaveType)
                        .balanceYear(nextYear)
                        .totalAllocated(allocated)
                        .totalUsed(0.0)
                        .totalPending(0.0)
                        .carriedForward(carryAmount)
                        .availableBalance(allocated + carryAmount)
                        .isActive(true)
                        .createdBy(processedBy)
                        .build();
                leaveBalanceRepository.save(newBalance);
            }
            processedCount++;
        }
        log.info("Year-end carry-forward complete. Processed {} balances for year {}",
                processedCount, year);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // LEAVE APPLICATION
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public LeaveApplicationResponse applyLeave(LeaveApplicationRequest request,
                                               String createdBy) {
        log.info("Applying leave for empId: {} from: {} to: {}",
                request.getEmpId(), request.getFromDate(), request.getToDate());

        validateEmployeeExists(request.getEmpId());

        // Date validation
        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date");
        }

        LeaveType leaveType = findLeaveTypeById(request.getLeaveTypeId());

        // Half-day validation
        boolean isHalfDay = Boolean.TRUE.equals(request.getIsHalfDay());
        if (isHalfDay && !Boolean.TRUE.equals(leaveType.getIsHalfDayAllowed())) {
            throw new IllegalArgumentException(
                    "Half-day leave is not allowed for type: "
                            + leaveType.getLeaveTypeName());
        }

        // Notice period validation
        if (leaveType.getMinNoticeDays() != null && leaveType.getMinNoticeDays() > 0) {
            long daysUntilLeave = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), request.getFromDate());
            if (daysUntilLeave < leaveType.getMinNoticeDays()) {
                throw new IllegalArgumentException(
                        "Minimum " + leaveType.getMinNoticeDays()
                                + " day(s) advance notice required for "
                                + leaveType.getLeaveTypeName());
            }
        }

        // Overlap check
        if (leaveApplicationRepository.hasOverlappingLeave(
                request.getEmpId(), request.getFromDate(), request.getToDate())) {
            throw new DuplicateResourceException(
                    "A leave application already exists for the selected date range");
        }

        // Calculate leave days
        double leaveDays = isHalfDay ? 0.5
                : calculateLeaveDays(request.getFromDate(), request.getToDate());

        // Balance check
        int currentYear = LocalDate.now().getYear();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmpIdAndLeaveTypeIdAndBalanceYear(
                        request.getEmpId(), leaveType.getId(), currentYear)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave balance not initialized for empId: " + request.getEmpId()
                                + " and leave type: " + leaveType.getLeaveTypeName()
                                + ". Please contact HR."));

        if (balance.getAvailableBalance() < leaveDays) {
            throw new IllegalArgumentException(
                    "Insufficient leave balance. Available: "
                            + balance.getAvailableBalance()
                            + " days, Requested: " + leaveDays + " days");
        }

        // Create application
        LeaveApplication application = LeaveApplication.builder()
                .empId(request.getEmpId().trim())
                .leaveType(leaveType)
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .numberOfDays(leaveDays)
                .isHalfDay(isHalfDay)
                .reason(request.getReason().trim())
                .status(LeaveStatus.PENDING)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        LeaveApplication saved = leaveApplicationRepository.save(application);

        // Reserve balance as pending
        balance.setTotalPending(balance.getTotalPending() + leaveDays);
        balance.recalculateAvailableBalance();
        leaveBalanceRepository.save(balance);

        log.info("Leave application created with ID: {}", saved.getId());
        return mapApplicationToResponse(saved,
                resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public LeaveApplicationResponse getLeaveApplicationById(Long id) {
        LeaveApplication la = findApplicationById(id);
        return mapApplicationToResponse(la, resolveCreatedBy(la.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveApplicationResponse> getLeaveApplicationsByEmpId(
            String empId, int page, int size) {
        log.info("Fetching leave applications for empId:{} page:{} size:{}", empId, page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        Page<LeaveApplication> applications =
                leaveApplicationRepository
                        .findByEmpIdAndIsActiveTrueOrderByCreatedAtDesc(empId, pageable);
        return applications.map(la ->
                mapApplicationToResponse(la, resolveCreatedBy(la.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveApplicationResponse> getPendingApplications(int page, int size) {
        log.info("Fetching all pending leave applications page:{} size:{}", page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").ascending());
        return leaveApplicationRepository
                .findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
                        LeaveStatus.PENDING, pageable)
                .map(la -> mapApplicationToResponse(la,
                        resolveCreatedBy(la.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveApplicationResponse> getAllLeaveApplications(int page, int size) {
        log.info("Fetching all leave applications page:{} size:{}", page, size);
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return leaveApplicationRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(pageable)
                .map(la -> mapApplicationToResponse(la,
                        resolveCreatedBy(la.getCreatedBy())));
    }

    @Override
    @Transactional
    public LeaveApplicationResponse processLeaveAction(Long id, LeaveActionRequest request,
                                                       String reviewedBy) {
        log.info("Processing leave action: {} for application ID: {} by: {}",
                request.getAction(), id, reviewedBy);

        LeaveApplication application = findApplicationById(id);

        // Only PENDING applications can be actioned
        if (application.getStatus() != LeaveStatus.PENDING
                && request.getAction() != LeaveStatus.REVOKED) {
            throw new IllegalStateException(
                    "Cannot action a leave application with status: "
                            + application.getStatus());
        }

        LeaveStatus newStatus = request.getAction();

        // Only APPROVED and REJECTED and REVOKED are valid actions
        if (newStatus != LeaveStatus.APPROVED
                && newStatus != LeaveStatus.REJECTED
                && newStatus != LeaveStatus.REVOKED) {
            throw new IllegalArgumentException(
                    "Invalid action. Allowed: APPROVED, REJECTED, REVOKED");
        }

        LeaveBalance balance = leaveBalanceRepository
                .findByEmpIdAndLeaveTypeIdAndBalanceYear(
                        application.getEmpId(),
                        application.getLeaveType().getId(),
                        application.getFromDate().getYear())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave balance not found for this application"));

        double leaveDays = application.getNumberOfDays();

        if (newStatus == LeaveStatus.APPROVED) {
            // Deduct from pending → move to used
            balance.setTotalPending(
                    Math.max(0, balance.getTotalPending() - leaveDays));
            balance.setTotalUsed(balance.getTotalUsed() + leaveDays);

        } else if (newStatus == LeaveStatus.REJECTED
                || newStatus == LeaveStatus.REVOKED) {
            // Release the pending hold back to available
            balance.setTotalPending(
                    Math.max(0, balance.getTotalPending() - leaveDays));
            // If REVOKED after APPROVED — also subtract from used
            if (newStatus == LeaveStatus.REVOKED
                    && application.getStatus() == LeaveStatus.APPROVED) {
                balance.setTotalUsed(
                        Math.max(0, balance.getTotalUsed() - leaveDays));
            }
        }

        balance.recalculateAvailableBalance();
        leaveBalanceRepository.save(balance);

        application.setStatus(newStatus);
        application.setReviewedBy(reviewedBy);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewerRemarks(request.getRemarks());

        LeaveApplication saved = leaveApplicationRepository.save(application);
        log.info("Leave application ID: {} updated to status: {}", id, newStatus);
        return mapApplicationToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public LeaveApplicationResponse cancelLeaveApplication(Long id, String cancelledBy) {
        log.info("Cancelling leave application ID: {} by: {}", id, cancelledBy);

        LeaveApplication application = findApplicationById(id);

        if (application.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException(
                    "Only PENDING applications can be cancelled. " +
                            "Current status: " + application.getStatus());
        }

        // Release the pending balance hold
        leaveBalanceRepository
                .findByEmpIdAndLeaveTypeIdAndBalanceYear(
                        application.getEmpId(),
                        application.getLeaveType().getId(),
                        application.getFromDate().getYear())
                .ifPresent(balance -> {
                    double days = application.getNumberOfDays();
                    balance.setTotalPending(
                            Math.max(0, balance.getTotalPending() - days));
                    balance.recalculateAvailableBalance();
                    leaveBalanceRepository.save(balance);
                });

        application.setStatus(LeaveStatus.CANCELLED);
        application.setReviewedBy(cancelledBy);
        application.setReviewedAt(LocalDateTime.now());
        application.setReviewerRemarks("Cancelled by employee");

        LeaveApplication saved = leaveApplicationRepository.save(application);
        log.info("Leave application ID: {} cancelled", id);
        return mapApplicationToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private void validateEmployeeExists(String empId) {
        if (!employeeRepository.existsByEmpId(empId)) {
            throw new ResourceNotFoundException(
                    "Employee not found with empId: " + empId);
        }
    }

    private LeaveType findLeaveTypeById(Long id) {
        return leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave type not found with ID: " + id));
    }

    private LeaveApplication findApplicationById(Long id) {
        return leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Leave application not found with ID: " + id));
    }

    /**
     * Calculate working days between two dates (inclusive).
     * Excludes Sundays only — Saturday is a working day at TBCPL.
     * Can be extended to also exclude holidays via HolidayRepository.
     */
    private double calculateLeaveDays(LocalDate from, LocalDate to) {
        long days = 0;
        LocalDate current = from;
        while (!current.isAfter(to)) {
            if (current.getDayOfWeek() != java.time.DayOfWeek.SUNDAY) {
                days++;
            }
            current = current.plusDays(1);
        }
        return days;
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return Collections.emptyMap();
        return nameResolver.resolve(Set.of(createdBy));
    }

    private LeaveBalanceResponse mapBalanceToResponse(LeaveBalance lb) {
        return LeaveBalanceResponse.builder()
                .id(lb.getId())
                .empId(lb.getEmpId())
                .leaveTypeId(lb.getLeaveType().getId())
                .leaveTypeName(lb.getLeaveType().getLeaveTypeName())
                .category(lb.getLeaveType().getCategory())
                .balanceYear(lb.getBalanceYear())
                .totalAllocated(lb.getTotalAllocated())
                .totalUsed(lb.getTotalUsed())
                .totalPending(lb.getTotalPending())
                .carriedForward(lb.getCarriedForward())
                .availableBalance(lb.getAvailableBalance())
                .isActive(lb.getIsActive())
                .createdAt(lb.getCreatedAt())
                .updatedAt(lb.getUpdatedAt())
                .build();
    }

    private LeaveApplicationResponse mapApplicationToResponse(
            LeaveApplication la, Map<String, String> nameMap) {
        String raw = la.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(
                        raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return LeaveApplicationResponse.builder()
                .id(la.getId())
                .empId(la.getEmpId())
                .leaveTypeId(la.getLeaveType().getId())
                .leaveTypeName(la.getLeaveType().getLeaveTypeName())
                .leaveCategory(la.getLeaveType().getCategory().name())
                .fromDate(la.getFromDate())
                .toDate(la.getToDate())
                .numberOfDays(la.getNumberOfDays())
                .isHalfDay(la.getIsHalfDay())
                .reason(la.getReason())
                .status(la.getStatus())
                .reviewedBy(la.getReviewedBy())
                .reviewedAt(la.getReviewedAt())
                .reviewerRemarks(la.getReviewerRemarks())
                .isActive(la.getIsActive())
                .createdAt(la.getCreatedAt())
                .updatedAt(la.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}