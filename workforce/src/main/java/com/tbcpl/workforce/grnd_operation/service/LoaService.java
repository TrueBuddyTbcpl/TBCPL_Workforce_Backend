package com.tbcpl.workforce.grnd_operation.service;

import com.tbcpl.workforce.grnd_operation.dto.request.LoaRequestDto;
import com.tbcpl.workforce.grnd_operation.dto.response.ClientDropdownDto;
import com.tbcpl.workforce.grnd_operation.dto.response.EmployeeDropdownDto;
import com.tbcpl.workforce.grnd_operation.dto.response.LoaResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoaService {

    /** Dropdown: active FIELD_ASSOCIATE employees — Admin dept + Admin/SuperAdmin role */
    List<EmployeeDropdownDto> getFieldAssociateDropdown();

    /** Dropdown: active clients — Admin dept + Admin/SuperAdmin role */
    List<ClientDropdownDto> getClientDropdown();

    /** Create LOA (DRAFT) — Admin dept + Admin/SuperAdmin role */
    LoaResponseDto createLoa(LoaRequestDto request);

    /** Update LOA — only while DRAFT — Admin dept + Admin/SuperAdmin role */
    LoaResponseDto updateLoa(Long id, LoaRequestDto request);

    /** Transition DRAFT → FINALIZED — Admin dept + Admin/SuperAdmin role */
    LoaResponseDto finalizeLoa(Long id);

    /**
     * Send finalized LOA PDF via email to the assigned employee.
     * Both conditions must hold: (Admin OR Operation dept) AND (Associate/Manager/Admin/SuperAdmin role).
     */
    void sendLoaByMail(Long id);

    /** Generate and return PDF bytes — all authenticated users */
    byte[] previewLoaPdf(Long id);

    /** Paginated list of all active LOAs — all authenticated users */
    Page<LoaResponseDto> getAllLoas(Pageable pageable);

    /** Single LOA by id — all authenticated users */
    LoaResponseDto getLoaById(Long id);
}
