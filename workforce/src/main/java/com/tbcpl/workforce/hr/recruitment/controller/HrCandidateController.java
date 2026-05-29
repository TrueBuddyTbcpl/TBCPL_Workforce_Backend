package com.tbcpl.workforce.hr.recruitment.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrCandidateRequest;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrInterviewScheduleRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrCandidateResponse;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrInterviewScheduleResponse;
import com.tbcpl.workforce.hr.recruitment.service.HrCandidateService;
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
public class HrCandidateController {

    private final HrCandidateService candidateService;

    /** POST /api/v1/hr/candidates */
    @PostMapping(ApiEndpoints.HR_CANDIDATES)
    public ResponseEntity<ApiResponse<HrCandidateResponse>> addCandidate(
            @Valid @RequestBody HrCandidateRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Add candidate: {} by: {}", request.getFullName(), createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Candidate added successfully",
                        candidateService.addCandidate(request, createdBy)));
    }

    /** GET /api/v1/hr/candidates?requisitionId=1&page=0&size=20 */
    @GetMapping(ApiEndpoints.HR_CANDIDATES)
    public ResponseEntity<ApiResponse<Page<HrCandidateResponse>>> getCandidates(
            @RequestParam(required = false)    Long requisitionId,
            @RequestParam(defaultValue = "0")  int  page,
            @RequestParam(defaultValue = "20") int  size
    ) {
        Page<HrCandidateResponse> result = (requisitionId != null)
                ? candidateService.getCandidatesByRequisition(requisitionId, page, size)
                : candidateService.getAllCandidates(page, size);
        return ResponseEntity.ok(ApiResponse.success("Candidates retrieved", result));
    }

    /** GET /api/v1/hr/candidates/{id} */
    @GetMapping(ApiEndpoints.HR_CANDIDATE_BY_ID)
    public ResponseEntity<ApiResponse<HrCandidateResponse>> getCandidateById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Candidate retrieved",
                candidateService.getCandidateById(id)));
    }

    /** PUT /api/v1/hr/candidates/{id} */
    @PutMapping(ApiEndpoints.HR_CANDIDATE_BY_ID)
    public ResponseEntity<ApiResponse<HrCandidateResponse>> updateCandidate(
            @PathVariable Long id,
            @Valid @RequestBody HrCandidateRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        return ResponseEntity.ok(ApiResponse.success("Candidate updated successfully",
                candidateService.updateCandidate(id, request, updatedBy)));
    }

    /** PATCH /api/v1/hr/candidates/{id}/offer */
    @PatchMapping(ApiEndpoints.HR_CANDIDATE_BY_ID + "/offer")
    public ResponseEntity<ApiResponse<HrCandidateResponse>> updateOfferStatus(
            @PathVariable Long id,
            @RequestBody HrCandidateRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update offer status for candidate ID: {} by: {}", id, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Offer status updated",
                candidateService.updateOfferStatus(id, request, updatedBy)));
    }

    /** PATCH /api/v1/hr/candidates/{id}/joined?empId=2026/001 */
    @PatchMapping(ApiEndpoints.HR_CANDIDATE_BY_ID + "/joined")
    public ResponseEntity<ApiResponse<HrCandidateResponse>> markAsJoined(
            @PathVariable Long id,
            @RequestParam String empId,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Mark candidate ID: {} as JOINED with empId: {} by: {}", id, empId, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Candidate marked as joined",
                candidateService.markAsJoined(id, empId, updatedBy)));
    }

    /** DELETE /api/v1/hr/candidates/{id} */
    @DeleteMapping(ApiEndpoints.HR_CANDIDATE_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteCandidate(
            @PathVariable Long id
    ) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok(ApiResponse.success("Candidate deleted successfully"));
    }

    // ── Interview Schedule ────────────────────────────────────────────────────

    /** POST /api/v1/hr/interviews */
    @PostMapping(ApiEndpoints.HR_INTERVIEWS)
    public ResponseEntity<ApiResponse<HrInterviewScheduleResponse>> scheduleInterview(
            @Valid @RequestBody HrInterviewScheduleRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Schedule interview for candidate ID: {} round: {} by: {}",
                request.getCandidateId(), request.getRound(), createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview scheduled successfully",
                        candidateService.scheduleInterview(request, createdBy)));
    }

    /** GET /api/v1/hr/interviews/{id} */
    @GetMapping(ApiEndpoints.HR_INTERVIEW_BY_ID)
    public ResponseEntity<ApiResponse<HrInterviewScheduleResponse>> getInterviewById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Interview retrieved",
                candidateService.getInterviewById(id)));
    }

    /** GET /api/v1/hr/interviews/candidate/{candidateId} */
    @GetMapping("/interviews/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<List<HrInterviewScheduleResponse>>> getInterviewsByCandidate(
            @PathVariable Long candidateId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Interviews retrieved",
                candidateService.getInterviewsByCandidate(candidateId)));
    }

    /** PUT /api/v1/hr/interviews/{id} */
    @PutMapping(ApiEndpoints.HR_INTERVIEW_BY_ID)
    public ResponseEntity<ApiResponse<HrInterviewScheduleResponse>> updateInterview(
            @PathVariable Long id,
            @Valid @RequestBody HrInterviewScheduleRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(ApiResponse.success("Interview updated successfully",
                candidateService.updateInterview(id, request, authentication.getName())));
    }

    /** PATCH /api/v1/hr/interviews/{id}/feedback */
    @PatchMapping(ApiEndpoints.HR_INTERVIEW_BY_ID + "/feedback")
    public ResponseEntity<ApiResponse<HrInterviewScheduleResponse>> submitFeedback(
            @PathVariable Long id,
            @RequestBody HrInterviewScheduleRequest request,
            Authentication authentication
    ) {
        log.info("Submit feedback for interview ID: {} by: {}", id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Feedback submitted successfully",
                candidateService.submitFeedback(id, request, authentication.getName())));
    }

    /** DELETE /api/v1/hr/interviews/{id} */
    @DeleteMapping(ApiEndpoints.HR_INTERVIEW_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteInterview(
            @PathVariable Long id
    ) {
        candidateService.deleteInterview(id);
        return ResponseEntity.ok(ApiResponse.success("Interview deleted successfully"));
    }
}