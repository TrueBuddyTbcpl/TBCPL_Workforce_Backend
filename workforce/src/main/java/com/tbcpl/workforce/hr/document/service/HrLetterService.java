package com.tbcpl.workforce.hr.document.service;

import com.tbcpl.workforce.hr.document.dto.request.HrLetterRecordRequest;
import com.tbcpl.workforce.hr.document.dto.response.HrLetterRecordResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HrLetterService {

    /**
     * Issue a new letter to an employee.
     * Auto-generates reference number.
     */
    HrLetterRecordResponse issueLetter(HrLetterRecordRequest request, String issuedBy);

    HrLetterRecordResponse getLetterById(Long id);

    HrLetterRecordResponse getLetterByReferenceNumber(String referenceNumber);

    /**
     * Get all letters issued to a specific employee.
     */
    List<HrLetterRecordResponse> getLettersByEmpId(String empId);

    Page<HrLetterRecordResponse> getAllLetters(int page, int size);

    Page<HrLetterRecordResponse> getLettersByType(String letterType, int page, int size);

    /**
     * Update letter content or file URL before acknowledgement.
     */
    HrLetterRecordResponse updateLetter(Long id, HrLetterRecordRequest request,
                                        String updatedBy);

    /**
     * Employee acknowledges receipt of the letter.
     */
    HrLetterRecordResponse acknowledgeLetter(Long id, String acknowledgedBy);

    void deleteLetter(Long id);
}