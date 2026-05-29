package com.tbcpl.workforce.hr.recruitment.service.impl;

import com.tbcpl.workforce.common.exception.DuplicateResourceException;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrCandidateRequest;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrInterviewScheduleRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrCandidateResponse;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrInterviewScheduleResponse;
import com.tbcpl.workforce.hr.recruitment.entity.HrCandidate;
import com.tbcpl.workforce.hr.recruitment.entity.HrInterviewSchedule;
import com.tbcpl.workforce.hr.recruitment.entity.HrJobRequisition;
import com.tbcpl.workforce.hr.recruitment.entity.enums.InterviewStatus;
import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferStatus;
import com.tbcpl.workforce.hr.recruitment.entity.enums.RecruitmentStatus;
import com.tbcpl.workforce.hr.recruitment.repository.HrCandidateRepository;
import com.tbcpl.workforce.hr.recruitment.repository.HrInterviewScheduleRepository;
import com.tbcpl.workforce.hr.recruitment.repository.HrJobRequisitionRepository;
import com.tbcpl.workforce.hr.recruitment.service.HrCandidateService;
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
public class HrCandidateServiceImpl implements HrCandidateService {

    private final HrCandidateRepository           candidateRepository;
    private final HrInterviewScheduleRepository   interviewRepository;
    private final HrJobRequisitionRepository      requisitionRepository;
    private final EmployeeNameResolverService      nameResolver;

    @Override
    @Transactional
    public HrCandidateResponse addCandidate(HrCandidateRequest request, String createdBy) {
        log.info("Adding candidate: {} for requisition ID: {}",
                request.getFullName(), request.getJobRequisitionId());

        HrJobRequisition requisition = findRequisitionById(request.getJobRequisitionId());

        if (requisition.getStatus() != RecruitmentStatus.OPEN
                && requisition.getStatus() != RecruitmentStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Cannot add candidate to a requisition with status: "
                            + requisition.getStatus());
        }

        if (candidateRepository.existsByEmailAndJobRequisitionId(
                request.getEmail(), request.getJobRequisitionId())) {
            throw new DuplicateResourceException(
                    "Candidate with email: " + request.getEmail()
                            + " already exists for this requisition");
        }

        HrCandidate candidate = HrCandidate.builder()
                .jobRequisition(requisition)
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone().trim())
                .currentCompany(request.getCurrentCompany())
                .currentDesignation(request.getCurrentDesignation())
                .totalExperienceYears(request.getTotalExperienceYears())
                .currentCtc(request.getCurrentCtc())
                .expectedCtc(request.getExpectedCtc())
                .noticePeriodDays(request.getNoticePeriodDays())
                .resumeUrl(request.getResumeUrl())
                .linkedinUrl(request.getLinkedinUrl())
                .source(request.getSource())
                .referredBy(request.getReferredBy())
                .hrRemarks(request.getHrRemarks())
                .status(RecruitmentStatus.IN_PROGRESS)
                .offerStatus(OfferStatus.NOT_OFFERED)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        // Move requisition to IN_PROGRESS if still OPEN
        if (requisition.getStatus() == RecruitmentStatus.OPEN) {
            requisition.setStatus(RecruitmentStatus.IN_PROGRESS);
            requisitionRepository.save(requisition);
        }

        HrCandidate saved = candidateRepository.save(candidate);
        log.info("Candidate added with ID: {}", saved.getId());
        return mapCandidateToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrCandidateResponse getCandidateById(Long id) {
        HrCandidate c = candidateRepository.findByIdWithRequisition(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate not found with ID: " + id));
        return mapCandidateToResponse(c, resolveCreatedBy(c.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrCandidateResponse> getAllCandidates(int page, int size) {
        return candidateRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(c -> mapCandidateToResponse(c, resolveCreatedBy(c.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrCandidateResponse> getCandidatesByRequisition(Long requisitionId,
                                                                int page, int size) {
        return candidateRepository
                .findByJobRequisitionIdAndIsActiveTrueOrderByCreatedAtDesc(
                        requisitionId, PageRequest.of(page, size))
                .map(c -> mapCandidateToResponse(c, resolveCreatedBy(c.getCreatedBy())));
    }

    @Override
    @Transactional
    public HrCandidateResponse updateCandidate(Long id, HrCandidateRequest request,
                                               String updatedBy) {
        log.info("Updating candidate ID: {} by: {}", id, updatedBy);
        HrCandidate c = findCandidateById(id);

        if (request.getFullName()            != null) c.setFullName(request.getFullName().trim());
        if (request.getPhone()               != null) c.setPhone(request.getPhone());
        if (request.getCurrentCompany()      != null) c.setCurrentCompany(request.getCurrentCompany());
        if (request.getCurrentDesignation()  != null) c.setCurrentDesignation(request.getCurrentDesignation());
        if (request.getTotalExperienceYears() != null) c.setTotalExperienceYears(request.getTotalExperienceYears());
        if (request.getCurrentCtc()          != null) c.setCurrentCtc(request.getCurrentCtc());
        if (request.getExpectedCtc()         != null) c.setExpectedCtc(request.getExpectedCtc());
        if (request.getNoticePeriodDays()    != null) c.setNoticePeriodDays(request.getNoticePeriodDays());
        if (request.getResumeUrl()           != null) c.setResumeUrl(request.getResumeUrl());
        if (request.getLinkedinUrl()         != null) c.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getSource()              != null) c.setSource(request.getSource());
        if (request.getReferredBy()          != null) c.setReferredBy(request.getReferredBy());
        if (request.getHrRemarks()           != null) c.setHrRemarks(request.getHrRemarks());
        if (request.getRejectionReason()     != null) c.setRejectionReason(request.getRejectionReason());
        c.setCreatedBy(updatedBy);

        HrCandidate saved = candidateRepository.save(c);
        log.info("Candidate ID: {} updated", id);
        return mapCandidateToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrCandidateResponse updateOfferStatus(Long id, HrCandidateRequest request,
                                                 String updatedBy) {
        log.info("Updating offer status for candidate ID: {} to: {}",
                id, request.getOfferStatus());
        HrCandidate c = findCandidateById(id);

        if (request.getOfferStatus()        != null) c.setOfferStatus(request.getOfferStatus());
        if (request.getOfferedCtc()         != null) c.setOfferedCtc(request.getOfferedCtc());
        if (request.getOfferDate()          != null) c.setOfferDate(request.getOfferDate());
        if (request.getExpectedJoiningDate() != null) c.setExpectedJoiningDate(request.getExpectedJoiningDate());
        if (request.getRejectionReason()    != null) c.setRejectionReason(request.getRejectionReason());
        c.setCreatedBy(updatedBy);

        HrCandidate saved = candidateRepository.save(c);
        log.info("Offer status for candidate ID: {} updated to: {}", id, request.getOfferStatus());
        return mapCandidateToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrCandidateResponse markAsJoined(Long id, String empId, String updatedBy) {
        log.info("Marking candidate ID: {} as JOINED with empId: {}", id, empId);
        HrCandidate c = findCandidateById(id);

        c.setOfferStatus(OfferStatus.JOINED);
        c.setStatus(RecruitmentStatus.FILLED);
        c.setEmpId(empId);
        c.setCreatedBy(updatedBy);

        // Increment filled positions on requisition
        HrJobRequisition req = c.getJobRequisition();
        req.setFilledPositions(req.getFilledPositions() + 1);
        if (req.getFilledPositions() >= req.getNumberOfPositions()) {
            req.setStatus(RecruitmentStatus.FILLED);
        }
        requisitionRepository.save(req);

        HrCandidate saved = candidateRepository.save(c);
        log.info("Candidate ID: {} marked as JOINED", id);
        return mapCandidateToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteCandidate(Long id) {
        log.info("Soft deleting candidate ID: {}", id);
        HrCandidate c = findCandidateById(id);
        c.setIsActive(false);
        candidateRepository.save(c);
    }

    // ── Interview Schedule ────────────────────────────────────────────────────

    @Override
    @Transactional
    public HrInterviewScheduleResponse scheduleInterview(HrInterviewScheduleRequest request,
                                                         String createdBy) {
        log.info("Scheduling {} interview for candidate ID: {}",
                request.getRound(), request.getCandidateId());

        HrCandidate candidate = findCandidateById(request.getCandidateId());

        // Check interviewer conflict (±1 hour window)
        if (request.getInterviewerEmpId() != null) {
            java.time.LocalDateTime from = request.getScheduledAt().minusMinutes(30);
            java.time.LocalDateTime to   = request.getScheduledAt().plusMinutes(90);
            if (interviewRepository.hasInterviewerConflict(
                    request.getInterviewerEmpId(), from, to)) {
                throw new IllegalStateException(
                        "Interviewer has a conflicting interview scheduled at this time");
            }
        }

        HrInterviewSchedule schedule = HrInterviewSchedule.builder()
                .candidate(candidate)
                .round(request.getRound())
                .scheduledAt(request.getScheduledAt())
                .interviewerEmpId(request.getInterviewerEmpId())
                .interviewerName(request.getInterviewerName())
                .mode(request.getMode())
                .meetingLink(request.getMeetingLink())
                .venue(request.getVenue())
                .status(InterviewStatus.SCHEDULED)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrInterviewSchedule saved = interviewRepository.save(schedule);
        log.info("Interview scheduled with ID: {}", saved.getId());
        return mapInterviewToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrInterviewScheduleResponse getInterviewById(Long id) {
        HrInterviewSchedule i = findInterviewById(id);
        return mapInterviewToResponse(i, resolveCreatedBy(i.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HrInterviewScheduleResponse> getInterviewsByCandidate(Long candidateId) {
        return interviewRepository
                .findByCandidateIdAndIsActiveTrueOrderByScheduledAtAsc(candidateId)
                .stream()
                .map(i -> mapInterviewToResponse(i, resolveCreatedBy(i.getCreatedBy())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HrInterviewScheduleResponse updateInterview(Long id,
                                                       HrInterviewScheduleRequest request,
                                                       String updatedBy) {
        log.info("Updating interview ID: {} by: {}", id, updatedBy);
        HrInterviewSchedule i = findInterviewById(id);

        if (request.getScheduledAt()      != null) i.setScheduledAt(request.getScheduledAt());
        if (request.getInterviewerEmpId() != null) i.setInterviewerEmpId(request.getInterviewerEmpId());
        if (request.getInterviewerName()  != null) i.setInterviewerName(request.getInterviewerName());
        if (request.getMode()             != null) i.setMode(request.getMode());
        if (request.getMeetingLink()      != null) i.setMeetingLink(request.getMeetingLink());
        if (request.getVenue()            != null) i.setVenue(request.getVenue());
        if (request.getStatus()           != null) i.setStatus(request.getStatus());
        i.setCreatedBy(updatedBy);

        HrInterviewSchedule saved = interviewRepository.save(i);
        return mapInterviewToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public HrInterviewScheduleResponse submitFeedback(Long id,
                                                      HrInterviewScheduleRequest request,
                                                      String updatedBy) {
        log.info("Submitting feedback for interview ID: {} by: {}", id, updatedBy);
        HrInterviewSchedule i = findInterviewById(id);

        if (i.getStatus() == InterviewStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot submit feedback for a CANCELLED interview");
        }

        i.setStatus(InterviewStatus.COMPLETED);
        i.setFeedback(request.getFeedback());
        i.setScore(request.getScore());
        i.setResult(request.getResult());
        i.setCreatedBy(updatedBy);

        HrInterviewSchedule saved = interviewRepository.save(i);
        log.info("Feedback submitted for interview ID: {}", id);
        return mapInterviewToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteInterview(Long id) {
        log.info("Soft deleting interview ID: {}", id);
        HrInterviewSchedule i = findInterviewById(id);
        i.setIsActive(false);
        interviewRepository.save(i);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrCandidate findCandidateById(Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate not found with ID: " + id));
    }

    private HrJobRequisition findRequisitionById(Long id) {
        return requisitionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job requisition not found with ID: " + id));
    }

    private HrInterviewSchedule findInterviewById(Long id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Interview schedule not found with ID: " + id));
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return Collections.emptyMap();
        return nameResolver.resolve(Set.of(createdBy));
    }

    private HrCandidateResponse mapCandidateToResponse(HrCandidate c,
                                                       Map<String, String> nameMap) {
        String raw = c.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        HrJobRequisition req = c.getJobRequisition();
        return HrCandidateResponse.builder()
                .id(c.getId())
                .jobRequisitionId(req != null ? req.getId() : null)
                .requisitionCode(req != null ? req.getRequisitionCode() : null)
                .jobTitle(req != null ? req.getJobTitle() : null)
                .fullName(c.getFullName())
                .email(c.getEmail())
                .phone(c.getPhone())
                .currentCompany(c.getCurrentCompany())
                .currentDesignation(c.getCurrentDesignation())
                .totalExperienceYears(c.getTotalExperienceYears())
                .currentCtc(c.getCurrentCtc())
                .expectedCtc(c.getExpectedCtc())
                .noticePeriodDays(c.getNoticePeriodDays())
                .resumeUrl(c.getResumeUrl())
                .linkedinUrl(c.getLinkedinUrl())
                .source(c.getSource())
                .referredBy(c.getReferredBy())
                .status(c.getStatus())
                .offerStatus(c.getOfferStatus())
                .offeredCtc(c.getOfferedCtc())
                .offerDate(c.getOfferDate())
                .expectedJoiningDate(c.getExpectedJoiningDate())
                .empId(c.getEmpId())
                .rejectionReason(c.getRejectionReason())
                .hrRemarks(c.getHrRemarks())
                .isActive(c.getIsActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }

    private HrInterviewScheduleResponse mapInterviewToResponse(HrInterviewSchedule i,
                                                               Map<String, String> nameMap) {
        String raw = i.getCreatedBy();
        String resolvedName = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        HrCandidate c = i.getCandidate();
        return HrInterviewScheduleResponse.builder()
                .id(i.getId())
                .candidateId(c != null ? c.getId() : null)
                .candidateName(c != null ? c.getFullName() : null)
                .candidateEmail(c != null ? c.getEmail() : null)
                .round(i.getRound())
                .scheduledAt(i.getScheduledAt())
                .interviewerEmpId(i.getInterviewerEmpId())
                .interviewerName(i.getInterviewerName())
                .mode(i.getMode())
                .meetingLink(i.getMeetingLink())
                .venue(i.getVenue())
                .status(i.getStatus())
                .feedback(i.getFeedback())
                .score(i.getScore())
                .result(i.getResult())
                .isActive(i.getIsActive())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .createdBy(resolvedName)
                .build();
    }
}