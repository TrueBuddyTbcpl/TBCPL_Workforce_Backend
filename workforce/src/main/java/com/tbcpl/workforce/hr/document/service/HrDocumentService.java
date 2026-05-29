package com.tbcpl.workforce.hr.document.service;

import com.tbcpl.workforce.hr.document.dto.request.HrDocumentVerificationRequest;
import com.tbcpl.workforce.hr.document.dto.request.HrEmployeeDocumentRequest;
import com.tbcpl.workforce.hr.document.dto.response.HrEmployeeDocumentResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HrDocumentService {

    /**
     * Upload / register a document for an employee.
     */
    HrEmployeeDocumentResponse uploadDocument(HrEmployeeDocumentRequest request,
                                              String createdBy);

    HrEmployeeDocumentResponse getDocumentById(Long id);

    /**
     * Get all documents for a given employee.
     */
    List<HrEmployeeDocumentResponse> getDocumentsByEmpId(String empId);

    /**
     * Get all documents pending verification (HR view).
     */
    Page<HrEmployeeDocumentResponse> getPendingDocuments(int page, int size);

    Page<HrEmployeeDocumentResponse> getAllDocuments(int page, int size);

    Page<HrEmployeeDocumentResponse> getDocumentsByEmpId(String empId, int page, int size);

    /**
     * HR verifies or rejects a document.
     */
    HrEmployeeDocumentResponse verifyDocument(Long id,
                                              HrDocumentVerificationRequest request,
                                              String verifiedBy);

    /**
     * HR requests re-upload of a document.
     */
    HrEmployeeDocumentResponse requestReUpload(Long id, String remarks, String requestedBy);

    /**
     * Replace an existing document with a new file URL.
     */
    HrEmployeeDocumentResponse replaceDocument(Long id, HrEmployeeDocumentRequest request,
                                               String updatedBy);

    void deleteDocument(Long id);
}