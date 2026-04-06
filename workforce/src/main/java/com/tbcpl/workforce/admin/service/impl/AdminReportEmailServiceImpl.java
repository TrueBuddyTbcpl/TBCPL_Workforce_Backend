// com.tbcpl.workforce.admin.service.impl.AdminReportEmailServiceImpl.java
package com.tbcpl.workforce.admin.service.impl;

import com.tbcpl.workforce.admin.dto.request.SendReportEmailRequest;
import com.tbcpl.workforce.admin.service.AdminReportEmailService;
import com.tbcpl.workforce.common.dto.ReportEmailRequestDto;
import com.tbcpl.workforce.common.email.ReportEmailService;
import com.tbcpl.workforce.common.exception.EmailDeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Service
public class AdminReportEmailServiceImpl implements AdminReportEmailService {

    private static final long MAX_PDF_SIZE_BYTES = 10 * 1024 * 1024L; // 10MB
    private static final String PDF_CONTENT_TYPE  = "application/pdf";

    private final ReportEmailService reportEmailService;

    public AdminReportEmailServiceImpl(ReportEmailService reportEmailService) {
        this.reportEmailService = reportEmailService;
    }

    @Override
    public void sendReport(SendReportEmailRequest request, MultipartFile pdfFile) {

        validatePdfFile(pdfFile);

        byte[] pdfBytes;
        try {
            pdfBytes = pdfFile.getBytes();
        } catch (IOException ex) {
            log.error("Failed to read uploaded PDF for case: {}", request.getCaseReference(), ex);
            throw new EmailDeliveryException("Failed to read uploaded PDF file.");
        }

        String sanitizedFileName = sanitizeFileName(
                Objects.requireNonNull(pdfFile.getOriginalFilename())
        );

        ReportEmailRequestDto emailRequestDto = ReportEmailRequestDto.builder()
                .reportType(request.getReportType())
                .toEmail(request.getToEmail())
                .toName(request.getToName())
                .clientName(request.getClientName())
                .caseReference(request.getCaseReference())
                .preparedBy(request.getPreparedBy())
                .reportDate(request.getReportDate())
                .pdfBytes(pdfBytes)
                .pdfFileName(sanitizedFileName)
                .build();

        log.info("Admin dispatching [{}] report email | Case: {} | To: {}",
                request.getReportType(), request.getCaseReference(), request.getToEmail());

        reportEmailService.sendReport(emailRequestDto);
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private void validatePdfFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmailDeliveryException("PDF file must not be empty.");
        }
        if (file.getSize() > MAX_PDF_SIZE_BYTES) {
            throw new EmailDeliveryException("PDF file size exceeds the 10MB limit.");
        }
        String contentType = file.getContentType();
        if (!PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            throw new EmailDeliveryException("Only PDF files are accepted. Received: " + contentType);
        }
    }

    private String sanitizeFileName(String originalName) {
        // Strip any path traversal, keep only the filename
        return originalName
                .replaceAll("[^a-zA-Z0-9._\\-]", "_")
                .replaceAll("\\.{2,}", ".");
    }
}