package com.tbcpl.workforce.operation.prereport.controller;

import com.tbcpl.workforce.common.response.ApiResponse;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportSendMailRequestDto;
import com.tbcpl.workforce.operation.prereport.service.PreReportEmailService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/operation/pre-reports")
public class OperationPreReportEmailController {

    private final PreReportEmailService preReportEmailService;

    public OperationPreReportEmailController(PreReportEmailService preReportEmailService) {
        this.preReportEmailService = preReportEmailService;
    }

    @PostMapping("/{reportId}/send-mail")
    public ResponseEntity<ApiResponse<String>> sendReportMail(
            @PathVariable String reportId,
            @Valid @RequestBody PreReportSendMailRequestDto request) {

        log.info("[EmailController] POST /pre-reports/{}/send-mail to={}",
                reportId, request.getToEmail());

        preReportEmailService.sendPreReportMail(reportId, request);

        return ResponseEntity.ok(ApiResponse.success(
                "Report sent successfully to " + request.getToEmail(), null));
    }
}