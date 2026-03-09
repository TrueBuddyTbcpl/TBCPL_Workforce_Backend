package com.tbcpl.workforce.operation.cases.controller;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.operation.cases.dto.request.AddCaseUpdateRequest;
import com.tbcpl.workforce.operation.cases.dto.request.CreateCaseRequest;
import com.tbcpl.workforce.operation.cases.dto.request.LinkProfileRequest;
import com.tbcpl.workforce.operation.cases.dto.response.*;
import com.tbcpl.workforce.operation.cases.service.CaseService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operation/cases")
@Slf4j
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping("/from-prereport/{prereportId}")
    public ResponseEntity<ApiResponse<CaseResponse>> createCaseFromPreReport(
            @PathVariable Long prereportId,
            @RequestBody CreateCaseRequest request,
            @RequestHeader("X-Username") String username
    ) {
        log.info("Create case request from prereport ID: {} by user: {}", prereportId, username);
        CaseResponse response = caseService.createCaseFromPreReport(prereportId, request, username);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Case created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CaseListItemResponse>>> getAllCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Cases fetched successfully",
                caseService.getAllCases(pageable)));
    }

    @GetMapping("/{caseId}")
    public ResponseEntity<ApiResponse<CaseResponse>> getCaseById(@PathVariable Long caseId) {
        return ResponseEntity.ok(ApiResponse.success("Case fetched successfully",
                caseService.getCaseById(caseId)));
    }

    @GetMapping("/by-number/{caseNumber}")
    public ResponseEntity<ApiResponse<CaseResponse>> getCaseByCaseNumber(
            @PathVariable String caseNumber) {
        return ResponseEntity.ok(ApiResponse.success("Case fetched successfully",
                caseService.getCaseByCaseNumber(caseNumber)));
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<ApiResponse<Page<CaseListItemResponse>>> getCasesByClient(
            @PathVariable Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Cases fetched successfully",
                caseService.getCasesByClientId(clientId, pageable)));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<ApiResponse<Page<CaseListItemResponse>>> getCasesByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Cases fetched successfully",
                caseService.getCasesByStatus(status, pageable)));
    }


    @GetMapping("/operations-employees")
    public ResponseEntity<ApiResponse<List<OperationsEmployeeResponse>>> getOperationsEmployees() {
        log.info("Fetching operations department employees");
        return ResponseEntity.ok(ApiResponse.success(
                "Operations employees fetched successfully",
                caseService.getOperationsEmployees()
        ));
    }


    @PostMapping("/{caseId}/updates")
    public ResponseEntity<ApiResponse<Void>> addCaseUpdate(
            @PathVariable Long caseId,
            @RequestBody AddCaseUpdateRequest request,
            @RequestHeader("X-Username") String username) {
        caseService.addUpdate(caseId, request, username);
        return ResponseEntity.ok(ApiResponse.success("Update added successfully", null));
    }

    // ── Add these 3 endpoints to existing CaseController ──────────────────

    @GetMapping("/{caseId}/documents")
    public ResponseEntity<ApiResponse<List<CaseDocumentResponse>>> getCaseDocuments(
            @PathVariable Long caseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Documents fetched successfully",
                caseService.getCaseDocuments(caseId)
        ));
    }

    @PostMapping(value = "/{caseId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<CaseDocumentResponse>> uploadDocument(
            @PathVariable Long caseId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-Username") String uploadedBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Document uploaded successfully",
                caseService.uploadDocument(caseId, file, uploadedBy)
        ));
    }

    @DeleteMapping("/{caseId}/documents/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable Long caseId,
            @PathVariable Long documentId,
            @RequestHeader("X-Username") String requestedBy) {
        caseService.deleteDocument(caseId, documentId, requestedBy);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully", null));
    }

    // Add 3 endpoints inside CaseController

    @GetMapping("/{caseId}/linked-profiles")
    public ResponseEntity<ApiResponse<List<LinkedProfileResponse>>> getLinkedProfiles(
            @PathVariable Long caseId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Linked profiles fetched",
                caseService.getLinkedProfiles(caseId)
        ));
    }

    @PostMapping("/{caseId}/linked-profiles")
    public ResponseEntity<ApiResponse<LinkedProfileResponse>> linkProfile(
            @PathVariable Long caseId,
            @RequestBody @Valid LinkProfileRequest request,
            @RequestHeader("X-Username") String linkedBy) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                "Profile linked successfully",
                caseService.linkProfile(caseId, request, linkedBy)
        ));
    }

    @DeleteMapping("/{caseId}/linked-profiles/{profileId}")
    public ResponseEntity<ApiResponse<Void>> unlinkProfile(
            @PathVariable Long caseId,
            @PathVariable Long profileId,
            @RequestHeader("X-Username") String requestedBy) {
        caseService.unlinkProfile(caseId, profileId, requestedBy);
        return ResponseEntity.ok(ApiResponse.success("Profile unlinked", null));
    }

    @GetMapping("/profile/{profileId}/count")
    public ResponseEntity<ApiResponse<Long>> countLinkedCasesForProfile(
            @PathVariable Long profileId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Count fetched",
                caseService.countLinkedCasesForProfile(profileId)
        ));
    }




}
