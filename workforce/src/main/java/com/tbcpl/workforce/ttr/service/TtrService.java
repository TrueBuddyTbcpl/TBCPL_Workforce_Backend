package com.tbcpl.workforce.ttr.service;

import com.tbcpl.workforce.ttr.dto.request.TtrChildCreateRequest;
import com.tbcpl.workforce.ttr.dto.request.TtrCreateRequest;
import com.tbcpl.workforce.ttr.dto.request.TtrStatusUpdateRequest;
import com.tbcpl.workforce.ttr.dto.response.TtrCompletionRecordResponse;
import com.tbcpl.workforce.ttr.dto.response.TtrDashboardResponse;
import com.tbcpl.workforce.ttr.dto.response.TtrResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TtrService {

    TtrResponse createParentTtr(TtrCreateRequest request, String createdByEmpId);

    TtrResponse createChildTtr(Long parentTtrId, TtrChildCreateRequest request, String createdByEmpId);



    TtrResponse updateStatus(Long ttrId, TtrStatusUpdateRequest request, String actorEmpId, String actorRole);

    TtrResponse updateStatusWithProof(Long ttrId, TtrStatusUpdateRequest request,
                                      MultipartFile proofFile,
                                      String actorEmpId, String actorRole);

    TtrResponse getTtrById(Long id);

    Page<TtrResponse> getAllTtrs(int page, int size, Long departmentId, String status, String assignedEmpId, String ttrType);

    Page<TtrResponse> getTtrsByDepartment(Long departmentId, int page, int size);

    List<TtrDashboardResponse> getDashboardMetrics();

    TtrDashboardResponse getDepartmentMetrics(Long departmentId);

    Page<TtrCompletionRecordResponse> getCompletionHistory(Long ttrId, Pageable pageable);
    // TtrService.java (interface)

}