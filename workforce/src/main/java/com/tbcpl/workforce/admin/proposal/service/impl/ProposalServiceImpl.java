package com.tbcpl.workforce.admin.proposal.service.impl;

import com.tbcpl.workforce.admin.proposal.dto.request.CreateProposalRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalSectionRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalStatusRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalSubSectionRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ReorderSectionsRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ReorderSubSectionsRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.UpdateProposalRequest;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalListItemResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalSectionResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalSubSectionResponse;
import com.tbcpl.workforce.admin.proposal.entity.Proposal;
import com.tbcpl.workforce.admin.proposal.entity.ProposalSection;
import com.tbcpl.workforce.admin.proposal.entity.ProposalSubSection;
import com.tbcpl.workforce.admin.proposal.entity.enums.ProposalStatus;
import com.tbcpl.workforce.admin.proposal.repository.ProposalRepository;
import com.tbcpl.workforce.admin.proposal.repository.ProposalSectionRepository;
import com.tbcpl.workforce.admin.proposal.repository.ProposalSubSectionRepository;
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
            ProposalStatus.DRAFT, Set.of(ProposalStatus.IN_PROGRESS),
            ProposalStatus.IN_PROGRESS, Set.of(ProposalStatus.WAITING_FOR_APPROVAL),
            ProposalStatus.WAITING_FOR_APPROVAL, Set.of(
                    ProposalStatus.APPROVED,
                    ProposalStatus.REQUEST_FOR_CHANGES,
                    ProposalStatus.DECLINED
            ),
            ProposalStatus.REQUEST_FOR_CHANGES, Set.of(
                    ProposalStatus.IN_PROGRESS,
                    ProposalStatus.WAITING_FOR_APPROVAL
            ),
            ProposalStatus.APPROVED, Set.of(),
            ProposalStatus.DECLINED, Set.of()
    );

    private final ProposalRepository proposalRepository;
    private final ProposalSectionRepository sectionRepository;
    private final ProposalSubSectionRepository subSectionRepository;

    @Override
    @Transactional
    public ProposalResponse create(CreateProposalRequest request, String createdBy) {
        Proposal proposal = Proposal.builder()
                .proposalCode(generateCode())
                .clientId(request.getClientId())
                .serviceType(request.getServiceType())
                .productName(request.getProductName())
                .status(ProposalStatus.DRAFT)
                .createdBy(createdBy)
                .updatedBy(createdBy)
                .build();

        if (request.getSections() != null && !request.getSections().isEmpty()) {
            List<ProposalSection> sections = buildSections(request.getSections(), proposal, createdBy);
            proposal.getSections().addAll(sections);
        }

        proposal = proposalRepository.save(proposal);
        log.info("Proposal created: {} by {}", proposal.getProposalCode(), createdBy);
        return toResponse(proposal);
    }

    @Override
    @Transactional(readOnly = true)
    public ProposalResponse getById(Long id) {
        return toResponse(findActive(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProposalListItemResponse> getAll(int page, int size) {
        return proposalRepository
                .findAllActive(PageRequest.of(page, size))
                .map(this::toListItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProposalListItemResponse> getByClientId(Long clientId, int page, int size) {
        return proposalRepository
                .findAllActiveByClientId(clientId, PageRequest.of(page, size))
                .map(this::toListItem);
    }

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

    @Override
    @Transactional
    public ProposalResponse update(Long id, UpdateProposalRequest request, String updatedBy) {
        Proposal proposal = findActive(id);

        if (request.getClientId() != null) {
            proposal.setClientId(request.getClientId());
        }

        proposal.setServiceType(request.getServiceType());
        proposal.setProductName(request.getProductName());

        if (request.getSections() != null) {
            proposal.getSections().clear();
            List<ProposalSection> fresh = buildSections(request.getSections(), proposal, updatedBy);
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

    @Override
    @Transactional
    public ProposalResponse updateStatus(Long id, ProposalStatusRequest request, String updatedBy) {
        Proposal proposal = findActive(id);
        ProposalStatus current = proposal.getStatus();
        ProposalStatus next = request.getStatus();

        Set<ProposalStatus> allowed = TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(next)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot transition from " + current + " to " + next
            );
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

    @Override
    @Transactional
    public void delete(Long id, String deletedBy) {
        Proposal proposal = findActive(id);
        proposal.setDeleted(true);
        proposal.setDeletedBy(deletedBy);
        proposalRepository.save(proposal);
        log.info("Proposal {} soft-deleted by {}", proposal.getProposalCode(), deletedBy);
    }

    @Override
    @Transactional
    public ProposalSectionResponse addSection(Long proposalId,
                                              ProposalSectionRequest request,
                                              String createdBy) {
        Proposal proposal = findActive(proposalId);

        int order = request.getDisplayOrder() != null
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

        if (request.getSubSections() != null && !request.getSubSections().isEmpty()) {
            List<ProposalSubSection> subSections = buildSubSections(
                    request.getSubSections(), section, createdBy);
            section.getSubSections().addAll(subSections);
        }

        section = sectionRepository.save(section);
        log.info("Section '{}' added to proposal {} by {}",
                section.getSectionKey(), proposalId, createdBy);
        return toSectionResponse(section);
    }

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

        if (request.getSubSections() != null) {
            section.getSubSections().clear();
            List<ProposalSubSection> fresh = buildSubSections(
                    request.getSubSections(), section, section.getCreatedBy());
            section.getSubSections().addAll(fresh);
        }

        section = sectionRepository.save(section);
        log.info("Section {} updated in proposal {}", sectionId, proposalId);
        return toSectionResponse(section);
    }

    @Override
    @Transactional
    public void deleteSection(Long proposalId, Long sectionId) {
        ProposalSection section = findSection(proposalId, sectionId);
        sectionRepository.delete(section);
        log.info("Section {} deleted from proposal {}", sectionId, proposalId);
    }

    @Override
    @Transactional
    public ProposalResponse reorderSections(Long proposalId, ReorderSectionsRequest request) {
        Proposal proposal = findActive(proposalId);

        List<Long> existingIds = proposal.getSections()
                .stream()
                .map(ProposalSection::getId)
                .toList();

        boolean allMatch = request.getSectionIds().stream().allMatch(existingIds::contains);

        if (!allMatch || request.getSectionIds().size() != existingIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "sectionIds must contain exactly all section IDs of this proposal"
            );
        }

        AtomicInteger order = new AtomicInteger(1);
        request.getSectionIds().forEach(id ->
                sectionRepository.updateDisplayOrder(id, order.getAndIncrement())
        );

        log.info("Sections reordered for proposal {}", proposalId);
        return toResponse(findActive(proposalId));
    }

    @Override
    @Transactional
    public ProposalSectionResponse toggleSectionVisibility(Long proposalId, Long sectionId) {
        ProposalSection section = findSection(proposalId, sectionId);
        section.setVisible(!section.isVisible());
        section = sectionRepository.save(section);
        log.info("Section {} visibility toggled to {} in proposal {}",
                sectionId, section.isVisible(), proposalId);
        return toSectionResponse(section);
    }

    @Override
    @Transactional
    public ProposalSubSectionResponse addSubSection(Long proposalId,
                                                    Long sectionId,
                                                    ProposalSubSectionRequest request,
                                                    String createdBy) {
        findActive(proposalId);
        ProposalSection section = findSection(proposalId, sectionId);

        int order = request.getDisplayOrder() != null
                ? request.getDisplayOrder()
                : subSectionRepository.findMaxDisplayOrder(sectionId) + 1;

        ProposalSubSection subSection = ProposalSubSection.builder()
                .section(section)
                .subSectionKey(request.getSubSectionKey())
                .subSectionTitle(request.getSubSectionTitle())
                .contentType(request.getContentType())
                .content(request.getContent())
                .displayOrder(order)
                .visible(request.isVisible())
                .createdBy(createdBy)
                .build();

        subSection = subSectionRepository.save(subSection);
        log.info("SubSection '{}' added to section {} in proposal {} by {}",
                subSection.getSubSectionKey(), sectionId, proposalId, createdBy);
        return toSubSectionResponse(subSection);
    }

    @Override
    @Transactional
    public ProposalSubSectionResponse updateSubSection(Long proposalId,
                                                       Long sectionId,
                                                       Long subSectionId,
                                                       ProposalSubSectionRequest request) {
        findActive(proposalId);
        findSection(proposalId, sectionId);
        ProposalSubSection subSection = findSubSection(sectionId, subSectionId);

        subSection.setSubSectionKey(request.getSubSectionKey());
        subSection.setSubSectionTitle(request.getSubSectionTitle());
        subSection.setContentType(request.getContentType());
        subSection.setContent(request.getContent());
        subSection.setVisible(request.isVisible());

        if (request.getDisplayOrder() != null) {
            subSection.setDisplayOrder(request.getDisplayOrder());
        }

        subSection = subSectionRepository.save(subSection);
        log.info("SubSection {} updated in section {} of proposal {}",
                subSectionId, sectionId, proposalId);
        return toSubSectionResponse(subSection);
    }

    @Override
    @Transactional
    public void deleteSubSection(Long proposalId, Long sectionId, Long subSectionId) {
        findActive(proposalId);
        findSection(proposalId, sectionId);
        ProposalSubSection subSection = findSubSection(sectionId, subSectionId);
        subSectionRepository.delete(subSection);
        log.info("SubSection {} deleted from section {} of proposal {}",
                subSectionId, sectionId, proposalId);
    }

    @Override
    @Transactional
    public ProposalSectionResponse reorderSubSections(Long proposalId,
                                                      Long sectionId,
                                                      ReorderSubSectionsRequest request) {
        findActive(proposalId);
        ProposalSection section = findSection(proposalId, sectionId);

        List<Long> existingIds = section.getSubSections()
                .stream()
                .map(ProposalSubSection::getId)
                .toList();

        boolean allMatch = request.getSubSectionIds().stream().allMatch(existingIds::contains);

        if (!allMatch || request.getSubSectionIds().size() != existingIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "subSectionIds must contain exactly all subsection IDs of this section"
            );
        }

        AtomicInteger order = new AtomicInteger(1);
        request.getSubSectionIds().forEach(subId ->
                subSectionRepository.updateDisplayOrder(subId, order.getAndIncrement())
        );

        log.info("SubSections reordered for section {} in proposal {}", sectionId, proposalId);
        return toSectionResponse(findSection(proposalId, sectionId));
    }

    @Override
    @Transactional
    public ProposalSubSectionResponse toggleSubSectionVisibility(Long proposalId,
                                                                 Long sectionId,
                                                                 Long subSectionId) {
        findActive(proposalId);
        findSection(proposalId, sectionId);
        ProposalSubSection subSection = findSubSection(sectionId, subSectionId);
        subSection.setVisible(!subSection.isVisible());
        subSection = subSectionRepository.save(subSection);
        log.info("SubSection {} visibility toggled to {} in section {} of proposal {}",
                subSectionId, subSection.isVisible(), sectionId, proposalId);
        return toSubSectionResponse(subSection);
    }

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

    private ProposalSubSection findSubSection(Long sectionId, Long subSectionId) {
        return subSectionRepository.findByIdAndSectionId(subSectionId, sectionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "SubSection " + subSectionId + " not found in section " + sectionId));
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
            int displayOrder = req.getDisplayOrder() != null
                    ? req.getDisplayOrder()
                    : order.getAndIncrement();

            ProposalSection section = ProposalSection.builder()
                    .proposal(proposal)
                    .sectionKey(req.getSectionKey())
                    .sectionTitle(req.getSectionTitle())
                    .contentType(req.getContentType())
                    .content(req.getContent())
                    .displayOrder(displayOrder)
                    .visible(req.isVisible())
                    .createdBy(createdBy)
                    .build();

            if (req.getSubSections() != null && !req.getSubSections().isEmpty()) {
                List<ProposalSubSection> subSections = buildSubSections(
                        req.getSubSections(), section, createdBy);
                section.getSubSections().addAll(subSections);
            }

            sections.add(section);
        }

        return sections;
    }

    private List<ProposalSubSection> buildSubSections(List<ProposalSubSectionRequest> requests,
                                                      ProposalSection section,
                                                      String createdBy) {
        List<ProposalSubSection> subSections = new ArrayList<>();
        AtomicInteger order = new AtomicInteger(1);

        for (ProposalSubSectionRequest req : requests) {
            int displayOrder = req.getDisplayOrder() != null
                    ? req.getDisplayOrder()
                    : order.getAndIncrement();

            subSections.add(ProposalSubSection.builder()
                    .section(section)
                    .subSectionKey(req.getSubSectionKey())
                    .subSectionTitle(req.getSubSectionTitle())
                    .contentType(req.getContentType())
                    .content(req.getContent())
                    .displayOrder(displayOrder)
                    .visible(req.isVisible())
                    .createdBy(createdBy)
                    .build());
        }

        return subSections;
    }

    private ProposalResponse toResponse(Proposal p) {
        return ProposalResponse.builder()
                .id(p.getId())
                .proposalCode(p.getProposalCode())
                .clientId(p.getClientId())
                .serviceType(p.getServiceType())
                .productName(p.getProductName())
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
                .serviceType(p.getServiceType())
                .productName(p.getProductName())
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
                .subSections(s.getSubSections().stream()
                        .map(this::toSubSectionResponse)
                        .toList())
                .createdBy(s.getCreatedBy())
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    private ProposalSubSectionResponse toSubSectionResponse(ProposalSubSection ss) {
        return ProposalSubSectionResponse.builder()
                .id(ss.getId())
                .subSectionKey(ss.getSubSectionKey())
                .subSectionTitle(ss.getSubSectionTitle())
                .contentType(ss.getContentType())
                .content(ss.getContent())
                .displayOrder(ss.getDisplayOrder())
                .visible(ss.isVisible())
                .createdBy(ss.getCreatedBy())
                .createdAt(ss.getCreatedAt())
                .updatedAt(ss.getUpdatedAt())
                .build();
    }
}