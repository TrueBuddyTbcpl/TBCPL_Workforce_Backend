package com.tbcpl.workforce.hr.recruitment.service;

import com.tbcpl.workforce.hr.recruitment.dto.request.HrCandidateRequest;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrInterviewScheduleRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrCandidateResponse;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrInterviewScheduleResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HrCandidateService {

    HrCandidateResponse addCandidate(HrCandidateRequest request, String createdBy);

    HrCandidateResponse getCandidateById(Long id);

    Page<HrCandidateResponse> getAllCandidates(int page, int size);

    Page<HrCandidateResponse> getCandidatesByRequisition(Long requisitionId,
                                                         int page, int size);

    HrCandidateResponse updateCandidate(Long id, HrCandidateRequest request, String updatedBy);

    /**
     * Update offer details for a candidate.
     */
    HrCandidateResponse updateOfferStatus(Long id, HrCandidateRequest request, String updatedBy);

    /**
     * Mark candidate as JOINED and link empId.
     * Also increments filledPositions on the requisition.
     */
    HrCandidateResponse markAsJoined(Long id, String empId, String updatedBy);

    void deleteCandidate(Long id);

    // ── Interview Schedule ────────────────────────────────────────────────────

    HrInterviewScheduleResponse scheduleInterview(HrInterviewScheduleRequest request,
                                                  String createdBy);

    HrInterviewScheduleResponse getInterviewById(Long id);

    List<HrInterviewScheduleResponse> getInterviewsByCandidate(Long candidateId);

    HrInterviewScheduleResponse updateInterview(Long id, HrInterviewScheduleRequest request,
                                                String updatedBy);

    /**
     * Submit feedback after interview completion.
     */
    HrInterviewScheduleResponse submitFeedback(Long id, HrInterviewScheduleRequest request,
                                               String updatedBy);

    void deleteInterview(Long id);
}