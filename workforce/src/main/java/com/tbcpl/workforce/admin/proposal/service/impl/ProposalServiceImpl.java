package com.tbcpl.workforce.admin.proposal.service.impl;

import com.tbcpl.workforce.admin.proposal.dto.request.CreateProposalRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalSectionRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalStatusRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ReorderSectionsRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.UpdateProposalRequest;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalListItemResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalSectionResponse;
import com.tbcpl.workforce.admin.proposal.entity.Proposal;
import com.tbcpl.workforce.admin.proposal.entity.ProposalSection;
import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import com.tbcpl.workforce.admin.proposal.repository.ProposalRepository;
import com.tbcpl.workforce.admin.proposal.repository.ProposalSectionRepository;
import com.tbcpl.workforce.admin.proposal.service.ProposalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProposalServiceImpl implements ProposalService {

    private static final Map<ProposalStatus, Set<ProposalStatus>> TRANSITIONS = Map.of(
            ProposalStatus.DRAFT,                Set.of(ProposalStatus.IN_PROGRESS),
            ProposalStatus.IN_PROGRESS,          Set.of(ProposalStatus.WAITING_FOR_APPROVAL),
            ProposalStatus.WAITING_FOR_APPROVAL, Set.of(
                    ProposalStatus.APPROVED,
                    ProposalStatus.REQUEST_FOR_CHANGES,
                    ProposalStatus.DECLINED),
            ProposalStatus.REQUEST_FOR_CHANGES,  Set.of(
                    ProposalStatus.IN_PROGRESS,
                    ProposalStatus.WAITING_FOR_APPROVAL),
            ProposalStatus.APPROVED,             Set.of(),
            ProposalStatus.DECLINED,             Set.of()
    );

    private final ProposalRepository        proposalRepository;
    private final ProposalSectionRepository sectionRepository;

    // ── Create ────────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProposalResponse create(CreateProposalRequest request, String createdBy) {
        Proposal proposal = Proposal.builder()
                .proposalCode(generateCode())
                .clientId(request.getClientId())
                .status(ProposalStatus.DRAFT)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        if (request.getSections() != null && !request.getSections().isEmpty()) {
            List<ProposalSection> sections = buildSections(
                    request.getSections(), proposal, createdBy);
            proposal.getSections().addAll(sections);
        }

        proposal = proposalRepository.save(proposal);
        log.info("Proposal created: {} by {}", proposal.getProposalCode(), createdBy);
        return toResponse(proposal);
    }

    // ── Get By ID ─────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public ProposalResponse getById(Long id) {
        return toResponse(findActive(id));
    }

    // ── Get All ───────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Page<ProposalListItemResponse> getAll(int page, int size) {
        return proposalRepository
                .findAllActive(PageRequest.of(page, size))
                .map(this::toListItem);
    }

    // ── Get By Client ─────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Page<ProposalListItemResponse> getByClientId(Long clientId, int page, int size) {
        return proposalRepository
                .findAllActiveByClientId(clientId, PageRequest.of(page, size))
                .map(this::toListItem);
    }

    // ── Get By Status ─────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Page<ProposalListItemResponse> getByStatus(String status, int page, int size) {
        ProposalStatus ps;
        try {
            ps = ProposalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid status: " + status);
        }
        return proposalRepository
                .findAllActiveByStatus(ps, PageRequest.of(page, size))
                .map(this::toListItem);
    }

    // ── Update Proposal (full section replace if sections provided) ───────────
    @Override
    @Transactional
    public ProposalResponse update(Long id, UpdateProposalRequest request, String updatedBy) {
        Proposal proposal = findActive(id);

        if (request.getClientId() != null) {
            proposal.setClientId(request.getClientId());
        }

        if (request.getSections() != null) {
            // Full replace — clear existing, rebuild from request
            proposal.getSections().clear();
            List<ProposalSection> fresh = buildSections(
                    request.getSections(), proposal, updatedBy);
            proposal.getSections().addAll(fresh);
        }

        proposal.setUpdatedBy(updatedBy);

        if (ProposalStatus.DRAFT.equals(proposal.getStatus())) {
            proposal.setStatus(ProposalStatus.IN_PROGRESS);
        }

        proposal = proposalRepository.save(proposal);
        log.info("Proposal updated: {} by {}", proposal.getProposalCode(), updatedBy);
        return toResponse(proposal);
    }

    // ── Update Status ─────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProposalResponse updateStatus(Long id, ProposalStatusRequest request, String updatedBy) {
        Proposal       proposal = findActive(id);
        ProposalStatus current  = proposal.getStatus();
        ProposalStatus next     = request.getStatus();

        Set<ProposalStatus> allowed = TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(next)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot transition from " + current + " to " + next);
        }

        if (request.getRemarks() != null) {
            proposal.setRemarks(request.getRemarks());
        }

        proposal.setStatus(next);
        proposal.setUpdatedBy(updatedBy);
        proposal = proposalRepository.save(proposal);
        log.info("Proposal {} status: {} → {} by {}",
                proposal.getProposalCode(), current, next, updatedBy);
        return toResponse(proposal);
    }

    // ── Soft Delete ───────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id, String deletedBy) {
        Proposal proposal = findActive(id);
        proposal.setDeleted(true);
        proposal.setDeletedBy(deletedBy);
        proposalRepository.save(proposal);
        log.info("Proposal {} soft-deleted by {}", proposal.getProposalCode(), deletedBy);
    }

    // ── Add Section ───────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProposalSectionResponse addSection(Long proposalId,
                                              ProposalSectionRequest request,
                                              String createdBy) {
        Proposal proposal = findActive(proposalId);

        int order = (request.getDisplayOrder() != null)
                ? request.getDisplayOrder()
                : sectionRepository.findMaxDisplayOrder(proposalId) + 1;

        ProposalSection section = ProposalSection.builder()
                .proposal(proposal)
                .sectionKey(request.getSectionKey())
                .sectionTitle(request.getSectionTitle())
                .contentType(request.getContentType())
                .content(request.getContent())
                .displayOrder(order)
                .visible(request.isVisible())
                .createdBy(createdBy)
                .build();

        section = sectionRepository.save(section);
        log.info("Section '{}' added to proposal {} by {}",
                section.getSectionKey(), proposalId, createdBy);
        return toSectionResponse(section);
    }

    // ── Update Section ────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProposalSectionResponse updateSection(Long proposalId,
                                                 Long sectionId,
                                                 ProposalSectionRequest request) {
        ProposalSection section = findSection(proposalId, sectionId);

        section.setSectionKey(request.getSectionKey());
        section.setSectionTitle(request.getSectionTitle());
        section.setContentType(request.getContentType());
        section.setContent(request.getContent());
        section.setVisible(request.isVisible());

        if (request.getDisplayOrder() != null) {
            section.setDisplayOrder(request.getDisplayOrder());
        }

        section = sectionRepository.save(section);
        log.info("Section {} updated in proposal {}", sectionId, proposalId);
        return toSectionResponse(section);
    }

    // ── Delete Section ────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteSection(Long proposalId, Long sectionId) {
        ProposalSection section = findSection(proposalId, sectionId);
        sectionRepository.delete(section);
        log.info("Section {} deleted from proposal {}", sectionId, proposalId);
    }

    // ── Reorder Sections ──────────────────────────────────────────────────────
    @Override
    @Transactional
    public ProposalResponse reorderSections(Long proposalId, ReorderSectionsRequest request) {
        Proposal proposal = findActive(proposalId);

        List<Long> existingIds = proposal.getSections()
                .stream()
                .map(ProposalSection::getId)
                .toList();

        // Validate — all submitted IDs must belong to this proposal
        boolean allMatch = request.getSectionIds().stream()
                .allMatch(existingIds::contains);

        if (!allMatch || request.getSectionIds().size() != existingIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "sectionIds must contain exactly all section IDs of this proposal");
        }

        AtomicInteger order = new AtomicInteger(1);
        request.getSectionIds().forEach(sectionId ->
                sectionRepository.updateDisplayOrder(sectionId, order.getAndIncrement())
        );

        log.info("Sections reordered for proposal {}", proposalId);
        return toResponse(findActive(proposalId));
    }

    // ── Toggle Section Visibility ─────────────────────────────────────────────
    @Override
    @Transactional
    public ProposalSectionResponse toggleVisibility(Long proposalId, Long sectionId) {
        ProposalSection section = findSection(proposalId, sectionId);
        section.setVisible(!section.isVisible());
        section = sectionRepository.save(section);
        log.info("Section {} visibility toggled to {} in proposal {}",
                sectionId, section.isVisible(), proposalId);
        return toSectionResponse(section);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private Proposal findActive(Long id) {
        return proposalRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Proposal not found with id: " + id));
    }

    private ProposalSection findSection(Long proposalId, Long sectionId) {
        return sectionRepository.findByIdAndProposalId(sectionId, proposalId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Section " + sectionId + " not found in proposal " + proposalId));
    }

    private String generateCode() {
        long seq = proposalRepository.countByDeletedFalse() + 1;
        return String.format("PROP-%05d", seq);
    }

    private List<ProposalSection> buildSections(List<ProposalSectionRequest> requests,
                                                Proposal proposal,
                                                String createdBy) {
        List<ProposalSection> sections = new ArrayList<>();
        AtomicInteger order = new AtomicInteger(1);

        for (ProposalSectionRequest req : requests) {
            int displayOrder = (req.getDisplayOrder() != null)
                    ? req.getDisplayOrder()
                    : order.getAndIncrement();

            sections.add(ProposalSection.builder()
                    .proposal(proposal)
                    .sectionKey(req.getSectionKey())
                    .sectionTitle(req.getSectionTitle())
                    .contentType(req.getContentType())
                    .content(req.getContent())
                    .displayOrder(displayOrder)
                    .visible(req.isVisible())
                    .createdBy(createdBy)
                    .build());
        }
        return sections;
    }

    private ProposalResponse toResponse(Proposal p) {
        return ProposalResponse.builder()
                .id(p.getId())
                .proposalCode(p.getProposalCode())
                .clientId(p.getClientId())
                .status(p.getStatus())
                .sections(p.getSections().stream()
                        .map(this::toSectionResponse)
                        .toList())
                .remarks(p.getRemarks())
                .createdBy(p.getCreatedBy())
                .updatedBy(p.getUpdatedBy())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ProposalListItemResponse toListItem(Proposal p) {
        return ProposalListItemResponse.builder()
                .id(p.getId())
                .proposalCode(p.getProposalCode())
                .clientId(p.getClientId())
                .status(p.getStatus())
                .createdBy(p.getCreatedBy())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ProposalSectionResponse toSectionResponse(ProposalSection s) {
        return ProposalSectionResponse.builder()
                .id(s.getId())
                .sectionKey(s.getSectionKey())
                .sectionTitle(s.getSectionTitle())
                .contentType(s.getContentType())
                .content(s.getContent())
                .displayOrder(s.getDisplayOrder())
                .visible(s.isVisible())
                .createdBy(s.getCreatedBy())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}