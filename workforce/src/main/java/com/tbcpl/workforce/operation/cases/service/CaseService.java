package com.tbcpl.workforce.operation.cases.service;

import com.tbcpl.workforce.operation.cases.dto.request.AddCaseUpdateRequest;
import com.tbcpl.workforce.operation.cases.dto.request.CreateCaseRequest;
import com.tbcpl.workforce.operation.cases.dto.request.LinkProfileRequest;
import com.tbcpl.workforce.operation.cases.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CaseService {

    CaseResponse createCaseFromPreReport(Long prereportId, CreateCaseRequest request, String createdBy);

    CaseResponse getCaseById(Long caseId);

    CaseResponse getCaseByCaseNumber(String caseNumber);

    Page<CaseListItemResponse> getAllCases(Pageable pageable);

    Page<CaseListItemResponse> getCasesByClientId(Long clientId, Pageable pageable);

    Page<CaseListItemResponse> getCasesByStatus(String status, Pageable pageable);

    void addUpdate(Long caseId, AddCaseUpdateRequest request, String updatedBy);

    // Add these 3 methods to existing CaseService interface

    List<CaseDocumentResponse> getCaseDocuments(Long caseId);

    CaseDocumentResponse uploadDocument(Long caseId, MultipartFile file, String uploadedBy);

    void deleteDocument(Long caseId, Long documentId, String requestedBy);

    List<OperationsEmployeeResponse> getOperationsEmployees();

    // Add these to CaseService interface
    List<LinkedProfileResponse> getLinkedProfiles(Long caseId);
    LinkedProfileResponse linkProfile(Long caseId, LinkProfileRequest request, String linkedBy);
    void unlinkProfile(Long caseId, Long profileId, String requestedBy);
    long countLinkedCasesForProfile(Long profileId);



}
