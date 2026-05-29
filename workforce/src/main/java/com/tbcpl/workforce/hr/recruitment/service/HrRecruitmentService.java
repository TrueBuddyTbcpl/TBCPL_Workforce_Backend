package com.tbcpl.workforce.hr.recruitment.service;

import com.tbcpl.workforce.hr.recruitment.dto.request.HrJobRequisitionRequest;
import com.tbcpl.workforce.hr.recruitment.dto.response.HrJobRequisitionResponse;
import org.springframework.data.domain.Page;

public interface HrRecruitmentService {

    HrJobRequisitionResponse createRequisition(HrJobRequisitionRequest request,
                                               String createdBy);

    HrJobRequisitionResponse getRequisitionById(Long id);

    HrJobRequisitionResponse getRequisitionByCode(String code);

    Page<HrJobRequisitionResponse> getAllRequisitions(int page, int size);

    Page<HrJobRequisitionResponse> getRequisitionsByStatus(String status, int page, int size);

    Page<HrJobRequisitionResponse> getRequisitionsByDepartment(
            String department, int page, int size);

    HrJobRequisitionResponse updateRequisition(Long id, HrJobRequisitionRequest request,
                                               String updatedBy);

    HrJobRequisitionResponse updateRequisitionStatus(Long id, String status,
                                                     String remarks, String updatedBy);

    void deleteRequisition(Long id);
}