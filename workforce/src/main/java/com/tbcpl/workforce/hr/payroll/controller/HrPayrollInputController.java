package com.tbcpl.workforce.hr.payroll.controller;

import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.hr.payroll.dto.request.HrPayrollInputRequest;
import com.tbcpl.workforce.hr.payroll.dto.response.HrPayrollInputResponse;
import com.tbcpl.workforce.hr.payroll.service.HrPayrollInputService;
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
public class HrPayrollInputController {

    private final HrPayrollInputService payrollInputService;

    /** POST /api/v1/hr/payroll-inputs */
    @PostMapping(ApiEndpoints.HR_PAYROLL_INPUTS)
    public ResponseEntity<ApiResponse<HrPayrollInputResponse>> addPayrollInput(
            @Valid @RequestBody HrPayrollInputRequest request,
            Authentication authentication
    ) {
        String createdBy = authentication.getName();
        log.info("Add payroll input for empId: {} by: {}", request.getEmpId(), createdBy);
        HrPayrollInputResponse response =
                payrollInputService.addPayrollInput(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payroll input added successfully", response));
    }

    /** GET /api/v1/hr/payroll-inputs/{id} */
    @GetMapping(ApiEndpoints.HR_PAYROLL_INPUT_BY_ID)
    public ResponseEntity<ApiResponse<HrPayrollInputResponse>> getPayrollInputById(
            @PathVariable Long id
    ) {
        log.info("Get payroll input by ID: {}", id);
        return ResponseEntity.ok(
                ApiResponse.success("Payroll input retrieved",
                        payrollInputService.getPayrollInputById(id)));
    }

    /**
     * GET /api/v1/hr/payroll-inputs?empId=2026/001&month=5&year=2026&page=0&size=20
     * Flexible query — by empId+month, by month only, or by empId only
     */
    @GetMapping(ApiEndpoints.HR_PAYROLL_INPUTS)
    public ResponseEntity<?> getPayrollInputs(
            @RequestParam(required = false)    String  empId,
            @RequestParam(required = false)    Integer month,
            @RequestParam(required = false)    Integer year,
            @RequestParam(defaultValue = "0")  int     page,
            @RequestParam(defaultValue = "20") int     size
    ) {
        log.info("Get payroll inputs - empId:{} month:{} year:{} page:{} size:{}",
                empId, month, year, page, size);

        if (empId != null && month != null && year != null) {
            List<HrPayrollInputResponse> inputs =
                    payrollInputService.getPayrollInputsByEmpAndMonth(empId, month, year);
            return ResponseEntity.ok(
                    ApiResponse.success("Payroll inputs retrieved", inputs));
        }

        if (month != null && year != null) {
            Page<HrPayrollInputResponse> inputs =
                    payrollInputService.getPayrollInputsByMonth(month, year, page, size);
            return ResponseEntity.ok(
                    ApiResponse.success("Payroll inputs retrieved", inputs));
        }

        if (empId != null) {
            Page<HrPayrollInputResponse> inputs =
                    payrollInputService.getPayrollInputsByEmpId(empId, page, size);
            return ResponseEntity.ok(
                    ApiResponse.success("Payroll inputs retrieved", inputs));
        }

        return ResponseEntity.badRequest()
                .body(ApiResponse.error("Provide at least empId or month+year as query params"));
    }

    /** GET /api/v1/hr/payroll-inputs/emp/{empId} */
    @GetMapping(ApiEndpoints.HR_PAYROLL_INPUTS_BY_EMP)
    public ResponseEntity<ApiResponse<Page<HrPayrollInputResponse>>> getPayrollInputsByEmpId(
            @PathVariable String empId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        log.info("Fetch payroll inputs for empId:{}", empId);
        return ResponseEntity.ok(ApiResponse.success(
                "Payroll inputs retrieved",
                payrollInputService.getPayrollInputsByEmpId(empId, page, size)));
    }

    /** PUT /api/v1/hr/payroll-inputs/{id} */
    @PutMapping(ApiEndpoints.HR_PAYROLL_INPUT_BY_ID)
    public ResponseEntity<ApiResponse<HrPayrollInputResponse>> updatePayrollInput(
            @PathVariable Long id,
            @Valid @RequestBody HrPayrollInputRequest request,
            Authentication authentication
    ) {
        String updatedBy = authentication.getName();
        log.info("Update payroll input ID: {} by: {}", id, updatedBy);
        HrPayrollInputResponse response =
                payrollInputService.updatePayrollInput(id, request, updatedBy);
        return ResponseEntity.ok(
                ApiResponse.success("Payroll input updated successfully", response));
    }

    /**
     * POST /api/v1/hr/payroll-inputs/submit?month=5&year=2026
     * Bulk submit all DRAFT inputs for a month to Accounts
     */
    @PostMapping(ApiEndpoints.HR_PAYROLL_INPUTS_SUBMIT)
    public ResponseEntity<ApiResponse<String>> submitPayrollInputs(
            @RequestParam Integer month,
            @RequestParam Integer year,
            Authentication authentication
    ) {
        String submittedBy = authentication.getName();
        log.info("Submit payroll inputs for month:{}/{} by:{}", month, year, submittedBy);
        int count = payrollInputService.submitPayrollInputsForMonth(month, year, submittedBy);
        return ResponseEntity.ok(
                ApiResponse.success(count + " payroll input(s) submitted to Accounts "
                        + "for " + month + "/" + year));
    }

    /** DELETE /api/v1/hr/payroll-inputs/{id} */
    @DeleteMapping(ApiEndpoints.HR_PAYROLL_INPUT_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deletePayrollInput(
            @PathVariable Long id
    ) {
        log.info("Soft delete payroll input ID: {}", id);
        payrollInputService.deletePayrollInput(id);
        return ResponseEntity.ok(
                ApiResponse.success("Payroll input deleted successfully"));
    }
}