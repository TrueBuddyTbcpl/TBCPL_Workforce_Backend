// com.tbcpl.workforce.admin.controller.AdminReportEmailController.java
package com.tbcpl.workforce.admin.controller;

import com.tbcpl.workforce.admin.dto.request.SendReportEmailRequest;
import com.tbcpl.workforce.admin.service.AdminReportEmailService;
import com.tbcpl.workforce.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/reports")
public class AdminReportEmailController {

    private final AdminReportEmailService adminReportEmailService;

    public AdminReportEmailController(AdminReportEmailService adminReportEmailService) {
        this.adminReportEmailService = adminReportEmailService;
    }

    /**
     * POST /api/v1/admin/reports/send-email
     *
     * Allowed roles: ADMIN, SUPER_ADMIN (enforced via JWT in future auth phase)
     *
     * Accepts multipart/form-data:
     *   - "request"  → JSON fields (ReportType, recipient info, case details)
     *   - "pdfFile"  → the actual PDF file
     */

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPER_ADMIN')")  // ← ADD THIS
    @PostMapping(
            value = "/send-email",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> sendReportEmail(
            @Valid @RequestPart("request") SendReportEmailRequest request,
            @RequestPart("pdfFile") MultipartFile pdfFile
    ) {
        adminReportEmailService.sendReport(request, pdfFile);
        return ResponseEntity.ok(
                ApiResponse.success("Report email sent successfully to " + request.getToEmail())
        );
    }
}