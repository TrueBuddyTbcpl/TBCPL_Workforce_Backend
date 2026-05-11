package com.tbcpl.workforce.admin.proposal.service;

import com.tbcpl.workforce.admin.proposal.dto.request.CreateProposalRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalSectionRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalStatusRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ReorderSectionsRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.UpdateProposalRequest;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalListItemResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalSectionResponse;
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
    ProposalSectionResponse toggleVisibility(Long proposalId, Long sectionId);
}