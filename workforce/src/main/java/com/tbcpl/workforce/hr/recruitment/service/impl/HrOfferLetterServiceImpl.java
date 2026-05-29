package com.tbcpl.workforce.hr.recruitment.service.impl;

import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.common.util.EmployeeNameResolverService;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrOfferLetterActionRequest;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrOfferLetterRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrOfferLetterResponse;
import com.tbcpl.workforce.hr.recruitment.entity.HrCandidate;
import com.tbcpl.workforce.hr.recruitment.entity.HrJobRequisition;
import com.tbcpl.workforce.hr.recruitment.entity.HrOfferLetter;
import com.tbcpl.workforce.hr.recruitment.entity.enums.OfferLetterStatus;
import com.tbcpl.workforce.hr.recruitment.repository.HrCandidateRepository;
import com.tbcpl.workforce.hr.recruitment.repository.HrJobRequisitionRepository;
import com.tbcpl.workforce.hr.recruitment.repository.HrOfferLetterRepository;
import com.tbcpl.workforce.hr.recruitment.service.HrOfferLetterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class HrOfferLetterServiceImpl implements HrOfferLetterService {

    private final HrOfferLetterRepository    offerLetterRepository;
    private final HrCandidateRepository      candidateRepository;
    private final HrJobRequisitionRepository requisitionRepository;
    private final EmployeeNameResolverService nameResolver;

    @Override
    @Transactional
    public HrOfferLetterResponse createOfferLetter(HrOfferLetterRequest request,
                                                   String createdBy) {
        log.info("Creating offer letter candidateId:{} requisitionId:{} by:{}",
                request.getCandidateId(), request.getJobRequisitionId(), createdBy);

        HrCandidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Candidate not found with ID: " + request.getCandidateId()));

        HrJobRequisition requisition = requisitionRepository
                .findById(request.getJobRequisitionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Job Requisition not found with ID: "
                                + request.getJobRequisitionId()));

        // Guard: no duplicate active offer for same candidate
        if (offerLetterRepository.existsByCandidateIdAndStatusAndIsActiveTrue(
                request.getCandidateId(), OfferLetterStatus.SENT)) {
            throw new IllegalStateException(
                    "An active offer letter already exists for this candidate");
        }

        HrOfferLetter offer = HrOfferLetter.builder()
                .candidate(candidate)
                .jobRequisition(requisition)
                .designationOffered(request.getDesignationOffered().trim())
                .departmentOffered(request.getDepartmentOffered().trim())
                .ctcOffered(request.getCtcOffered())
                .joiningDate(request.getJoiningDate())
                .offerExpiryDate(request.getOfferExpiryDate())
                .workLocation(request.getWorkLocation())
                .documentUrl(request.getDocumentUrl())
                .specialConditions(request.getSpecialConditions())
                .status(OfferLetterStatus.DRAFTED)
                .isActive(true)
                .createdBy(createdBy)
                .build();

        HrOfferLetter saved = offerLetterRepository.save(offer);
        log.info("Offer letter created ID:{} for candidateId:{}",
                saved.getId(), request.getCandidateId());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public HrOfferLetterResponse getOfferLetterById(Long id) {
        HrOfferLetter offer = offerLetterRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer letter not found with ID: " + id));
        return mapToResponse(offer, resolveCreatedBy(offer.getCreatedBy()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrOfferLetterResponse> getAllOfferLetters(int page, int size) {
        return offerLetterRepository
                .findByIsActiveTrueOrderByCreatedAtDesc(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))
                .map(o -> mapToResponse(o, resolveCreatedBy(o.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrOfferLetterResponse> getOfferLettersByStatus(String status,
                                                               int page, int size) {
        OfferLetterStatus offerStatus = OfferLetterStatus.valueOf(status.toUpperCase());
        return offerLetterRepository
                .findByStatusAndIsActiveTrueOrderByCreatedAtDesc(
                        offerStatus, PageRequest.of(page, size))
                .map(o -> mapToResponse(o, resolveCreatedBy(o.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrOfferLetterResponse> getOfferLettersByCandidate(Long candidateId,
                                                                  int page, int size) {
        return offerLetterRepository
                .findByCandidateIdAndIsActiveTrueOrderByCreatedAtDesc(
                        candidateId, PageRequest.of(page, size))
                .map(o -> mapToResponse(o, resolveCreatedBy(o.getCreatedBy())));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HrOfferLetterResponse> getOfferLettersByRequisition(Long requisitionId,
                                                                    int page, int size) {
        return offerLetterRepository
                .findByJobRequisitionIdAndIsActiveTrueOrderByCreatedAtDesc(
                        requisitionId, PageRequest.of(page, size))
                .map(o -> mapToResponse(o, resolveCreatedBy(o.getCreatedBy())));
    }

    @Override
    @Transactional
    public HrOfferLetterResponse updateOfferLetterAction(Long id,
                                                         HrOfferLetterActionRequest request,
                                                         String updatedBy) {
        log.info("Offer letter action ID:{} status:{} by:{}", id, request.getStatus(), updatedBy);
        HrOfferLetter offer = findById(id);

        // Terminal states — cannot change
        if (offer.getStatus() == OfferLetterStatus.REVOKED
                || offer.getStatus() == OfferLetterStatus.EXPIRED) {
            throw new IllegalStateException(
                    "Cannot update an offer letter that is " + offer.getStatus().name());
        }

        offer.setStatus(request.getStatus());

        if (request.getDocumentUrl() != null) {
            offer.setDocumentUrl(request.getDocumentUrl());
        }

        // Record candidate response timestamp on accept/reject
        if (request.getStatus() == OfferLetterStatus.ACCEPTED
                || request.getStatus() == OfferLetterStatus.REJECTED) {
            offer.setCandidateRemarks(request.getCandidateRemarks());
            offer.setCandidateRespondedAt(LocalDateTime.now());
        }

        offer.setCreatedBy(updatedBy);
        HrOfferLetter saved = offerLetterRepository.save(offer);
        log.info("Offer letter ID:{} action updated to:{}", id, saved.getStatus());
        return mapToResponse(saved, resolveCreatedBy(saved.getCreatedBy()));
    }

    @Override
    @Transactional
    public void deleteOfferLetter(Long id) {
        log.info("Soft deleting offer letter ID:{}", id);
        HrOfferLetter offer = findById(id);
        offer.setIsActive(false);
        offerLetterRepository.save(offer);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private HrOfferLetter findById(Long id) {
        return offerLetterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Offer letter not found with ID: " + id));
    }

    private Map<String, String> resolveCreatedBy(String createdBy) {
        if (createdBy == null || createdBy.isBlank()) return Collections.emptyMap();
        return nameResolver.resolve(Set.of(createdBy));
    }

    private HrOfferLetterResponse mapToResponse(HrOfferLetter o,
                                                Map<String, String> nameMap) {
        String raw  = o.getCreatedBy();
        String name = raw == null ? null :
                nameMap.getOrDefault(raw.contains("@") ? raw.toLowerCase() : raw, raw);

        return HrOfferLetterResponse.builder()
                .id(o.getId())
                .candidateId(o.getCandidate().getId())
                // ✅ Replace with
                .candidateName(o.getCandidate().getFullName())
                .candidateEmail(o.getCandidate().getEmail())
                .jobRequisitionId(o.getJobRequisition().getId())
                .jobTitle(o.getJobRequisition().getJobTitle())
                .designationOffered(o.getDesignationOffered())
                .departmentOffered(o.getDepartmentOffered())
                .ctcOffered(o.getCtcOffered())
                .joiningDate(o.getJoiningDate())
                .offerExpiryDate(o.getOfferExpiryDate())
                .workLocation(o.getWorkLocation())
                .documentUrl(o.getDocumentUrl())
                .specialConditions(o.getSpecialConditions())
                .candidateRemarks(o.getCandidateRemarks())
                .candidateRespondedAt(o.getCandidateRespondedAt())
                .status(o.getStatus())
                .isActive(o.getIsActive())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .createdBy(name)
                .build();
    }
}