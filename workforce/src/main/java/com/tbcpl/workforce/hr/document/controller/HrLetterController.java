package com.tbcpl.workforce.hr.document.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.document.dto.request.HrLetterRecordRequest;
import com.tbcpl.workforce.hr.document.dto.response.HrLetterRecordResponse;
import com.tbcpl.workforce.hr.document.service.HrLetterService;
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
public class HrLetterController {

    private final HrLetterService letterService;

    /** POST /api/v1/hr/letters */
    @PostMapping(ApiEndpoints.HR_LETTERS)
    public ResponseEntity<ApiResponse<HrLetterRecordResponse>> issueLetter(
            @Valid @RequestBody HrLetterRecordRequest request,
            Authentication authentication
    ) {
        String issuedBy = authentication.getName();
        log.info("Issue letter type:{} for empId:{} by:{}",
                request.getLetterType(), request.getEmpId(), issuedBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Letter issued successfully",
                        letterService.issueLetter(request, issuedBy)));
    }

    /** GET /api/v1/hr/letters?empId=2026/001&type=OFFER_LETTER&page=0&size=20 */
    @GetMapping(ApiEndpoints.HR_LETTERS)
    public ResponseEntity<ApiResponse<?>> getLetters(
            @RequestParam(required = false)    String empId,
            @RequestParam(required = false)    String type,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (empId != null) {
            List<HrLetterRecordResponse> result = letterService.getLettersByEmpId(empId);
            return ResponseEntity.ok(ApiResponse.success("Letters retrieved", result));
        }
        if (type != null) {
            Page<HrLetterRecordResponse> result = letterService.getLettersByType(type, page, size);
            return ResponseEntity.ok(ApiResponse.success("Letters retrieved", result));
        }
        Page<HrLetterRecordResponse> result = letterService.getAllLetters(page, size);
        return ResponseEntity.ok(ApiResponse.success("Letters retrieved", result));
    }

    /** GET /api/v1/hr/letters/{id} */
    @GetMapping(ApiEndpoints.HR_LETTER_BY_ID)
    public ResponseEntity<ApiResponse<HrLetterRecordResponse>> getLetterById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Letter retrieved",
                letterService.getLetterById(id)));
    }

    /** GET /api/v1/hr/letters/ref/{referenceNumber} */
    @GetMapping("/letters/ref/{referenceNumber}")
    public ResponseEntity<ApiResponse<HrLetterRecordResponse>> getByReferenceNumber(
            @PathVariable String referenceNumber
    ) {
        return ResponseEntity.ok(ApiResponse.success("Letter retrieved",
                letterService.getLetterByReferenceNumber(referenceNumber)));
    }

    /** PUT /api/v1/hr/letters/{id} */
    @PutMapping(ApiEndpoints.HR_LETTER_BY_ID)
    public ResponseEntity<ApiResponse<HrLetterRecordResponse>> updateLetter(
            @PathVariable Long id,
            @Valid @RequestBody HrLetterRecordRequest request,
            Authentication authentication
    ) {
        log.info("Update letter ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Letter updated successfully",
                letterService.updateLetter(id, request, authentication.getName())));
    }

    /** PATCH /api/v1/hr/letters/{id}/acknowledge */
    @PatchMapping(ApiEndpoints.HR_LETTER_BY_ID + "/acknowledge")
    public ResponseEntity<ApiResponse<HrLetterRecordResponse>> acknowledgeLetter(
            @PathVariable Long id,
            Authentication authentication
    ) {
        log.info("Acknowledge letter ID:{} by:{}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Letter acknowledged successfully",
                letterService.acknowledgeLetter(id, authentication.getName())));
    }

    /** DELETE /api/v1/hr/letters/{id} */
    @DeleteMapping(ApiEndpoints.HR_LETTER_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteLetter(
            @PathVariable Long id
    ) {
        letterService.deleteLetter(id);
        return ResponseEntity.ok(ApiResponse.success("Letter deleted successfully"));
    }
}