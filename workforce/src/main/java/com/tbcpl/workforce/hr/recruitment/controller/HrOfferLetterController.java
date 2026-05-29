package com.tbcpl.workforce.hr.recruitment.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrOfferLetterActionRequest;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrOfferLetterRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrOfferLetterResponse;
import com.tbcpl.workforce.hr.recruitment.service.HrOfferLetterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpoints.HR_BASE)
@RequiredArgsConstructor
@Slf4j
public class HrOfferLetterController {

    private final HrOfferLetterService offerLetterService;

    /** POST /api/v1/hr/offer-letters */
    @PostMapping(ApiEndpoints.HR_OFFER_LETTERS)
    public ResponseEntity<ApiResponse<HrOfferLetterResponse>> createOfferLetter(
            @Valid @RequestBody HrOfferLetterRequest request,
            Authentication authentication
    ) {
        log.info("Create offer letter candidateId:{} by:{}",
                request.getCandidateId(), authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Offer letter created successfully",
                        offerLetterService.createOfferLetter(request,
                                authentication.getName())));
    }

    /**
     * GET /api/v1/hr/offer-letters
     * Query params: status, candidateId, requisitionId, page, size
     */
    @GetMapping(ApiEndpoints.HR_OFFER_LETTERS)
    public ResponseEntity<ApiResponse<?>> getOfferLetters(
            @RequestParam(required = false)    String status,
            @RequestParam(required = false)    Long   candidateId,
            @RequestParam(required = false)    Long   requisitionId,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "20") int    size
    ) {
        if (candidateId != null) {
            Page<HrOfferLetterResponse> result =
                    offerLetterService.getOfferLettersByCandidate(candidateId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Offer letters retrieved", result));
        }
        if (requisitionId != null) {
            Page<HrOfferLetterResponse> result =
                    offerLetterService.getOfferLettersByRequisition(requisitionId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Offer letters retrieved", result));
        }
        if (status != null) {
            Page<HrOfferLetterResponse> result =
                    offerLetterService.getOfferLettersByStatus(status, page, size);
            return ResponseEntity.ok(ApiResponse.success("Offer letters retrieved", result));
        }
        Page<HrOfferLetterResponse> result =
                offerLetterService.getAllOfferLetters(page, size);
        return ResponseEntity.ok(ApiResponse.success("Offer letters retrieved", result));
    }

    /** GET /api/v1/hr/offer-letters/{id} */
    @GetMapping(ApiEndpoints.HR_OFFER_LETTER_BY_ID)
    public ResponseEntity<ApiResponse<HrOfferLetterResponse>> getOfferLetterById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Offer letter retrieved",
                offerLetterService.getOfferLetterById(id)));
    }

    /**
     * PATCH /api/v1/hr/offer-letters/{id}
     * Used by HR (SEND, REVOKE) and candidate (ACCEPT, REJECT)
     */
    @PatchMapping(ApiEndpoints.HR_OFFER_LETTER_BY_ID)
    public ResponseEntity<ApiResponse<HrOfferLetterResponse>> updateOfferLetterAction(
            @PathVariable Long id,
            @Valid @RequestBody HrOfferLetterActionRequest request,
            Authentication authentication
    ) {
        log.info("Offer letter action ID:{} status:{} by:{}",
                id, request.getStatus(), authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Offer letter updated successfully",
                offerLetterService.updateOfferLetterAction(id, request,
                        authentication.getName())));
    }

    /** DELETE /api/v1/hr/offer-letters/{id} */
    @DeleteMapping(ApiEndpoints.HR_OFFER_LETTER_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteOfferLetter(@PathVariable Long id) {
        offerLetterService.deleteOfferLetter(id);
        return ResponseEntity.ok(ApiResponse.success("Offer letter deleted successfully"));
    }
}