// com.tbcpl.workforce.common.email.ReportEmailService.java
package com.tbcpl.workforce.common.email;

import com.tbcpl.workforce.common.dto.ReportEmailRequestDto;
import com.tbcpl.workforce.common.email.template.*;
import com.tbcpl.workforce.common.enums.ReportType;
import com.tbcpl.workforce.common.exception.EmailDeliveryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ReportEmailService {

    private final BrevoEmailService brevoEmailService;
    private final Map<ReportType, ReportEmailTemplate> templateRegistry;

    public ReportEmailService(
            BrevoEmailService brevoEmailService,
            PreReportEmailTemplate preReportEmailTemplate
            // Inject future templates here as they are implemented
    ) {
        this.brevoEmailService = brevoEmailService;

        // Registry maps each ReportType to its template
        this.templateRegistry = Map.of(
                ReportType.PRE_REPORT, preReportEmailTemplate
                // Add more entries here as templates are built:
                // ReportType.FINAL_REPORT, finalReportEmailTemplate,
                // ReportType.PROPOSAL,     proposalEmailTemplate,
                // ReportType.LETTER_OF_AUTHORITY, loaEmailTemplate
        );
    }

    /**
     * Universal entry point. Routes to the correct HTML template
     * based on ReportType and sends via Brevo.
     */
    public void sendReport(ReportEmailRequestDto request) {
        ReportEmailTemplate template = templateRegistry.get(request.getReportType());

        if (template == null) {
            log.error("No email template registered for report type: {}", request.getReportType());
            throw new EmailDeliveryException(
                    "Email template not available for: " + request.getReportType().getDisplayName()
            );
        }

        log.info("Dispatching [{}] report email to: {} | Case: {}",
                request.getReportType(), request.getToEmail(), request.getCaseReference());

        String subject = template.buildSubject(request);
        String htmlBody = template.buildHtmlBody(request);

        brevoEmailService.sendReportEmail(
                request.getToEmail(),
                request.getToName(),
                subject,
                htmlBody,
                request.getPdfBytes(),
                request.getPdfFileName()
        );

        log.info("[{}] report email sent successfully to: {}",
                request.getReportType(), request.getToEmail());
    }
}