package com.tbcpl.workforce.admin.proposal.service;

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
import org.springframework.data.domain.Page;

public interface ProposalService {

    // ── Proposal CRUD ─────────────────────────────────────────────────────────
    ProposalResponse               create(CreateProposalRequest request, String createdBy);
    ProposalResponse               getById(Long id);
    Page<ProposalListItemResponse> getAll(int page, int size);
    Page<ProposalListItemResponse> getByClientId(Long clientId, int page, int size);
    Page<ProposalListItemResponse> getByStatus(String status, int page, int size);
    ProposalResponse               update(Long id, UpdateProposalRequest request, String updatedBy);
    ProposalResponse               updateStatus(Long id, ProposalStatusRequest request, String updatedBy);
    void                           delete(Long id, String deletedBy);

    // ── Section Management ────────────────────────────────────────────────────
    ProposalSectionResponse addSection(Long proposalId, ProposalSectionRequest request, String createdBy);
    ProposalSectionResponse updateSection(Long proposalId, Long sectionId, ProposalSectionRequest request);
    void                    deleteSection(Long proposalId, Long sectionId);
    ProposalResponse        reorderSections(Long proposalId, ReorderSectionsRequest request);
    ProposalSectionResponse toggleSectionVisibility(Long proposalId, Long sectionId);

    // ── SubSection Management ─────────────────────────────────────────────────
    ProposalSubSectionResponse addSubSection(Long proposalId, Long sectionId, ProposalSubSectionRequest request, String createdBy);
    ProposalSubSectionResponse updateSubSection(Long proposalId, Long sectionId, Long subSectionId, ProposalSubSectionRequest request);
    void                       deleteSubSection(Long proposalId, Long sectionId, Long subSectionId);
    ProposalSectionResponse    reorderSubSections(Long proposalId, Long sectionId, ReorderSubSectionsRequest request);
    ProposalSubSectionResponse toggleSubSectionVisibility(Long proposalId, Long sectionId, Long subSectionId);
}