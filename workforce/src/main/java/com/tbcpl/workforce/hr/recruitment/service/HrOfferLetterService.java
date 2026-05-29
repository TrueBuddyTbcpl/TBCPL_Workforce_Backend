package com.tbcpl.workforce.hr.recruitment.service;

import com.tbcpl.workforce.hr.recruitment.dto.request.HrOfferLetterActionRequest;
import com.tbcpl.workforce.hr.recruitment.dto.request.HrOfferLetterRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrOfferLetterResponse;
import org.springframework.data.domain.Page;

public interface HrOfferLetterService {

    HrOfferLetterResponse createOfferLetter(HrOfferLetterRequest request, String createdBy);

    HrOfferLetterResponse getOfferLetterById(Long id);

    Page<HrOfferLetterResponse> getAllOfferLetters(int page, int size);

    Page<HrOfferLetterResponse> getOfferLettersByStatus(String status, int page, int size);

    Page<HrOfferLetterResponse> getOfferLettersByCandidate(Long candidateId,
                                                           int page, int size);

    Page<HrOfferLetterResponse> getOfferLettersByRequisition(Long requisitionId,
                                                             int page, int size);

    /**
     * HR sends, revokes, or updates the offer letter.
     * Candidate accepts or rejects via the same action endpoint.
     */
    HrOfferLetterResponse updateOfferLetterAction(Long id,
                                                  HrOfferLetterActionRequest request,
                                                  String updatedBy);

    void deleteOfferLetter(Long id);
}