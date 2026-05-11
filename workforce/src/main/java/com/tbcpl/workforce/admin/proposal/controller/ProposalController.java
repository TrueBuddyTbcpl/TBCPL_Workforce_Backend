package com.tbcpl.workforce.admin.proposal.controller;

import com.tbcpl.workforce.admin.proposal.dto.request.CreateProposalRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalSectionRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ProposalStatusRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.ReorderSectionsRequest;
import com.tbcpl.workforce.admin.proposal.dto.request.UpdateProposalRequest;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalListItemResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalResponse;
import com.tbcpl.workforce.admin.proposal.dto.response.ProposalSectionResponse;
import com.tbcpl.workforce.admin.proposal.service.ProposalService;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/proposals")
@RequiredArgsConstructor
@Slf4j
public class ProposalController {

    private final ProposalService proposalService;

    // ── Proposal CRUD ─────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<ProposalResponse>> create(
            @Valid @RequestBody CreateProposalRequest request,
            @RequestHeader("X-Username") String username
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Proposal created",
                        proposalService.create(request, username)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProposalListItemResponse>>> getAll(
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size,
            @RequestParam(required = false)    String status,
            @RequestParam(required = false)    Long   clientId
    ) {
        // ── TEMPORARY DEBUG — REMOVE AFTER FIX ───────────────────────────
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        log.info("=== AUTH DEBUG: principal={}, authorities={}, authenticated={}",
                auth != null ? auth.getName()           : "NULL",
                auth != null ? auth.getAuthorities()    : "NULL",
                auth != null ? auth.isAuthenticated()   : "FALSE");
        // ─────────────────────────────────────────────────────────────────

        Page<ProposalListItemResponse> data;

        if (status != null) {
            data = proposalService.getByStatus(status, page, size);
        } else if (clientId != null) {
            data = proposalService.getByClientId(clientId, page, size);
        } else {
            data = proposalService.getAll(page, size);
        }

        return ResponseEntity.ok(ApiResponse.success("Proposals fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProposalResponse>> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Proposal fetched",
                proposalService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProposalResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProposalRequest request,
            @RequestHeader("X-Username") String username
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Proposal updated",
                proposalService.update(id, request, username)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProposalResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProposalStatusRequest request,
            @RequestHeader("X-Username") String username
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Status updated",
                proposalService.updateStatus(id, request, username)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @RequestHeader("X-Username") String username
    ) {
        proposalService.delete(id, username);
        return ResponseEntity.ok(ApiResponse.success("Proposal deleted", null));
    }

    // ── Section Management ────────────────────────────────────────────────────

    @PostMapping("/{id}/sections")
    public ResponseEntity<ApiResponse<ProposalSectionResponse>> addSection(
            @PathVariable Long id,
            @Valid @RequestBody ProposalSectionRequest request,
            @RequestHeader("X-Username") String username
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Section added",
                        proposalService.addSection(id, request, username)));
    }

    @PutMapping("/{id}/sections/{sectionId}")
    public ResponseEntity<ApiResponse<ProposalSectionResponse>> updateSection(
            @PathVariable Long id,
            @PathVariable Long sectionId,
            @Valid @RequestBody ProposalSectionRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Section updated",
                proposalService.updateSection(id, sectionId, request)));
    }

    @DeleteMapping("/{id}/sections/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSection(
            @PathVariable Long id,
            @PathVariable Long sectionId
    ) {
        proposalService.deleteSection(id, sectionId);
        return ResponseEntity.ok(ApiResponse.success("Section deleted", null));
    }

    @PatchMapping("/{id}/sections/reorder")
    public ResponseEntity<ApiResponse<ProposalResponse>> reorderSections(
            @PathVariable Long id,
            @Valid @RequestBody ReorderSectionsRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Sections reordered",
                proposalService.reorderSections(id, request)));
    }

    @PatchMapping("/{id}/sections/{sectionId}/visibility")
    public ResponseEntity<ApiResponse<ProposalSectionResponse>> toggleVisibility(
            @PathVariable Long id,
            @PathVariable Long sectionId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                "Section visibility toggled",
                proposalService.toggleVisibility(id, sectionId)));
    }
}