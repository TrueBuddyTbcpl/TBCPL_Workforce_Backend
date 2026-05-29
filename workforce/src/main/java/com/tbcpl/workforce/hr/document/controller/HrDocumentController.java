package com.tbcpl.workforce.hr.document.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.document.dto.request.HrDocumentVerificationRequest;
import com.tbcpl.workforce.hr.document.dto.request.HrEmployeeDocumentRequest;
import com.tbcpl.workforce.hr.document.dto.response.HrEmployeeDocumentResponse;
import com.tbcpl.workforce.hr.document.service.HrDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.HR_BASE)
@RequiredArgsConstructor
@Slf4j
public class HrDocumentController {

    private final HrDocumentService documentService;

    /** POST /api/v1/hr/documents */
    @PostMapping(ApiEndpoints.HR_DOCUMENTS)
    public ResponseEntity<ApiResponse<HrEmployeeDocumentResponse>> uploadDocument(
            @Valid @RequestBody HrEmployeeDocumentRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Upload document type:{} for empId:{} by:{}",
                request.getDocumentType(), request.getEmpId(), createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Document uploaded successfully",
                        documentService.uploadDocument(request, createdBy)));
    }

    /** GET /api/v1/hr/documents?empId=2026/001&status=PENDING_VERIFICATION&page=0&size=20 */
    @GetMapping(ApiEndpoints.HR_DOCUMENTS)
    public ResponseEntity<ApiResponse<?>> getDocuments(
            @RequestParam(required = false)    String empId,
            @RequestParam(required = false)    String status,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (empId != null) {
            List<HrEmployeeDocumentResponse> result =
                    documentService.getDocumentsByEmpId(empId);
            return ResponseEntity.ok(ApiResponse.success("Documents retrieved", result));
        }
        if ("PENDING_VERIFICATION".equalsIgnoreCase(status)) {
            Page<HrEmployeeDocumentResponse> result =
                    documentService.getPendingDocuments(page, size);
            return ResponseEntity.ok(ApiResponse.success("Pending documents retrieved", result));
        }
        Page<HrEmployeeDocumentResponse> result =
                documentService.getAllDocuments(page, size);
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved", result));
    }

    /** GET /api/v1/hr/documents/{id} */
    @GetMapping(ApiEndpoints.HR_DOCUMENT_BY_ID)
    public ResponseEntity<ApiResponse<HrEmployeeDocumentResponse>> getDocumentById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Document retrieved",
                documentService.getDocumentById(id)));
    }

    /** GET /api/v1/hr/documents/emp/{empId} */
    @GetMapping(ApiEndpoints.HR_DOCUMENTS_BY_EMP)
    public ResponseEntity<ApiResponse<Page<HrEmployeeDocumentResponse>>> getDocumentsByEmpId(
            @PathVariable String empId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Fetch documents for empId:{}", empId);
        return ResponseEntity.ok(ApiResponse.success(
                "Documents retrieved",
                documentService.getDocumentsByEmpId(empId, page, size)));
    }

    /** PATCH /api/v1/hr/documents/{id}/verify */
    @PatchMapping(ApiEndpoints.HR_DOCUMENT_BY_ID + "/verify")
    public ResponseEntity<ApiResponse<HrEmployeeDocumentResponse>> verifyDocument(
            @PathVariable Long id,
            @Valid @RequestBody HrDocumentVerificationRequest request,
            Authentication authentication
    ) {
        String verifiedBy = authentication.getName();
        log.info("Verify document ID:{} status:{} by:{}", id, request.getStatus(), verifiedBy);
        return ResponseEntity.ok(ApiResponse.success("Document verification updated",
                documentService.verifyDocument(id, request, verifiedBy)));
    }

    /** PATCH /api/v1/hr/documents/{id}/re-upload?remarks=ID not clear */
    @PatchMapping(ApiEndpoints.HR_DOCUMENT_BY_ID + "/re-upload")
    public ResponseEntity<ApiResponse<HrEmployeeDocumentResponse>> requestReUpload(
            @PathVariable Long id,
            @RequestParam(required = false) String remarks,
            Authentication authentication
    ) {
        log.info("Request re-upload for document ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Re-upload requested",
                documentService.requestReUpload(id, remarks, authentication.getName())));
    }

    /** PUT /api/v1/hr/documents/{id}/replace */
    @PutMapping(ApiEndpoints.HR_DOCUMENT_BY_ID + "/replace")
    public ResponseEntity<ApiResponse<HrEmployeeDocumentResponse>> replaceDocument(
            @PathVariable Long id,
            @Valid @RequestBody HrEmployeeDocumentRequest request,
            Authentication authentication
    ) {
        log.info("Replace document ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Document replaced successfully",
                documentService.replaceDocument(id, request, authentication.getName())));
    }

    /** DELETE /api/v1/hr/documents/{id} */
    @DeleteMapping(ApiEndpoints.HR_DOCUMENT_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable Long id
    ) {
        documentService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success("Document deleted successfully"));
    }
}