package com.tbcpl.workforce.operation.prereport.service;

import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
import com.tbcpl.workforce.operation.prereport.dto.request.PreReportSendMailRequestDto;
import com.tbcpl.workforce.operation.prereport.entity.PreReport;
import com.tbcpl.workforce.operation.prereport.entity.PreReportClientLead;
import com.tbcpl.workforce.operation.prereport.entity.PreReportTrueBuddyLead;
import com.tbcpl.workforce.operation.prereport.entity.enums.LeadType;
import com.tbcpl.workforce.operation.prereport.repository.PreReportClientLeadRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportRepository;
import com.tbcpl.workforce.operation.prereport.repository.PreReportTrueBuddyLeadRepository;
import com.tbcpl.workforce.operation.prereport.service.PreReportEmailService;
import com.tbcpl.workforce.operation.prereport.service.PreReportPdfService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class PreReportEmailServiceImpl implements PreReportEmailService {

    private final JavaMailSender mailSender;
    private final PreReportPdfService pdfService;
    private final PreReportRepository preReportRepository;
    private final PreReportClientLeadRepository clientLeadRepository;
    private final PreReportTrueBuddyLeadRepository trueBuddyLeadRepository;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name:True Buddy Consulting Pvt. Ltd.}")
    private String fromName;

    public PreReportEmailServiceImpl(
            JavaMailSender mailSender,
            PreReportPdfService pdfService,
            PreReportRepository preReportRepository,
            PreReportClientLeadRepository clientLeadRepository,
            PreReportTrueBuddyLeadRepository trueBuddyLeadRepository) {
        this.mailSender = mailSender;
        this.pdfService = pdfService;
        this.preReportRepository = preReportRepository;
        this.clientLeadRepository = clientLeadRepository;
        this.trueBuddyLeadRepository = trueBuddyLeadRepository;
    }

    @Override
    public void sendPreReportMail(String reportId, PreReportSendMailRequestDto request) {
        log.info("[EmailService] Preparing mail for reportId={} to={}", reportId, request.getToEmail());

        // 1. Fetch PreReport
        PreReport preReport = preReportRepository.findByReportIdWithClient(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("PreReport not found: " + reportId));

        // 2. Fetch lead data based on type
        PreReportClientLead clientLead = null;
        PreReportTrueBuddyLead trueBuddyLead = null;

        if (LeadType.CLIENT_LEAD == preReport.getLeadType()) {
            clientLead = clientLeadRepository.findByPrereportId(preReport.getId()).orElse(null);
        } else {
            trueBuddyLead = trueBuddyLeadRepository.findByPrereportId(preReport.getId()).orElse(null);
        }

        // 3. Generate PDF bytes
        byte[] pdfBytes = pdfService.generatePdf(preReport, clientLead, trueBuddyLead);
        log.info("[EmailService] PDF generated ({} bytes) for reportId={}", pdfBytes.length, reportId);

        // 4. Build and send email
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(request.getToEmail());
            helper.setSubject("Preliminary Lead Assessment Report – " + reportId + " | True Buddy Consulting");
            helper.setText(buildEmailHtml(preReport, request), true);

            String fileName = "PreReport_" + reportId + ".pdf";
            helper.addAttachment(fileName, new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            log.info("[EmailService] Mail sent successfully for reportId={} to={}", reportId, request.getToEmail());

        } catch (Exception e) {
            log.error("[EmailService] Failed to send mail for reportId={}", reportId, e);
            throw new RuntimeException("Failed to send report email: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HTML EMAIL TEMPLATE
    // ─────────────────────────────────────────────────────────────────────────

    private String buildEmailHtml(PreReport preReport, PreReportSendMailRequestDto request) {

        String clientName    = preReport.getClient() != null
                ? preReport.getClient().getClientName() : "N/A";
        String leadType      = LeadType.TRUEBUDDY_LEAD == preReport.getLeadType()
                ? "True Buddy Lead" : "Client Lead";
        String status        = preReport.getReportStatus() != null
                ? toTitleCase(preReport.getReportStatus().name()) : "N/A";
        String dateGenerated = preReport.getCreatedAt() != null
                ? preReport.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                : "N/A";
        int    currentYear   = LocalDateTime.now().getYear();

        // Optional notes block
        String notesBlock = "";
        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            notesBlock = """
            <tr>
              <td style="padding: 0 40px 28px;">
                <table width="100%%" cellpadding="0" cellspacing="0"
                       style="background:#f8f9fb; border-left:4px solid #c8972b;
                              border-radius:6px; overflow:hidden;">
                  <tr>
                    <td style="padding:14px 18px;">
                      <p style="margin:0 0 6px; font-size:12px; font-weight:700;
                                color:#0f2340; text-transform:uppercase; letter-spacing:0.5px;">
                        Message from Sender
                      </p>
                      <p style="margin:0; font-size:13.5px; color:#4a5568; line-height:1.7;">
                        %s
                      </p>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
            """.formatted(escapeHtml(request.getNotes()));
        }

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
              <title>Preliminary Lead Assessment Report</title>
            </head>
            <body style="margin:0; padding:0; background-color:#f0f2f5;
                         font-family:'Segoe UI', Arial, sans-serif;">

              <table width="100%%" cellpadding="0" cellspacing="0"
                     style="background:#f0f2f5; padding:40px 20px;">
                <tr>
                  <td align="center">
                    <table width="620" cellpadding="0" cellspacing="0"
                           style="background:#ffffff; border-radius:12px; overflow:hidden;
                                  box-shadow:0 4px 24px rgba(0,0,0,0.08); max-width:620px;">

                      <!-- ── HEADER ──────────────────────────────────────── -->
                      <tr>
                        <td style="background:#0f2340; padding:30px 40px 22px; text-align:center;">
                          <p style="margin:0 0 5px; font-size:20px; font-weight:700;
                                    color:#ffffff; letter-spacing:0.6px;">
                            TRUE BUDDY CONSULTING PVT. LTD.
                          </p>
                          <p style="margin:0; font-size:10.5px; color:#a8c4dc; font-style:italic;">
                            Due Diligence &nbsp;|&nbsp; IPR Investigation &nbsp;|&nbsp;
                            Fraud Investigation &nbsp;|&nbsp; Market Surveys
                          </p>
                        </td>
                      </tr>

                      <!-- ── GOLD BAR ─────────────────────────────────────── -->
                      <tr>
                        <td style="background:#c8972b; height:4px;
                                   font-size:0; line-height:0;">&nbsp;</td>
                      </tr>

                      <!-- ── GREETING ─────────────────────────────────────── -->
                      <tr>
                        <td style="padding:34px 40px 20px;">
                          <p style="margin:0 0 8px; font-size:16px; font-weight:700;
                                    color:#0f2340;">
                            Dear %s,
                          </p>
                          <p style="margin:0; font-size:14px; color:#4a5568; line-height:1.75;">
                            Please find attached the
                            <strong style="color:#0f2340;">Preliminary Lead Assessment Report</strong>
                            for <strong style="color:#0f2340;">%s</strong>.
                          </p>
                        </td>
                      </tr>

                      <!-- ── OPTIONAL NOTES ───────────────────────────────── -->
                      %s

                      <!-- ── REPORT DETAILS CARD ──────────────────────────── -->
                      <tr>
                        <td style="padding:0 40px 28px;">
                          <table width="100%%" cellpadding="0" cellspacing="0"
                                 style="border:1.5px solid #b8c8d8; border-radius:8px;
                                        overflow:hidden;">
                            <!-- Card header -->
                            <tr>
                              <td colspan="2"
                                  style="background:#0f2340; padding:11px 20px;">
                                <p style="margin:0; font-size:12px; font-weight:700;
                                          color:#ffffff; letter-spacing:0.6px;">
                                  REPORT DETAILS
                                </p>
                              </td>
                            </tr>
                            <!-- Row 1 -->
                            <tr style="background:#ffffff;">
                              <td style="padding:11px 20px; font-size:12px; font-weight:700;
                                         color:#0f2340; width:38%%;
                                         border-bottom:1px solid #e8eef5;">Report ID</td>
                              <td style="padding:11px 20px; font-size:12px; color:#1a1a1a;
                                         border-bottom:1px solid #e8eef5;">%s</td>
                            </tr>
                            <!-- Row 2 -->
                            <tr style="background:#f4f7fb;">
                              <td style="padding:11px 20px; font-size:12px; font-weight:700;
                                         color:#0f2340; border-bottom:1px solid #e8eef5;">
                                Client Name</td>
                              <td style="padding:11px 20px; font-size:12px; color:#1a1a1a;
                                         border-bottom:1px solid #e8eef5;">%s</td>
                            </tr>
                            <!-- Row 3 -->
                            <tr style="background:#ffffff;">
                              <td style="padding:11px 20px; font-size:12px; font-weight:700;
                                         color:#0f2340; border-bottom:1px solid #e8eef5;">
                                Lead Type</td>
                              <td style="padding:11px 20px; font-size:12px; color:#1a1a1a;
                                         border-bottom:1px solid #e8eef5;">%s</td>
                            </tr>
                            <!-- Row 4 -->
                            <tr style="background:#f4f7fb;">
                              <td style="padding:11px 20px; font-size:12px; font-weight:700;
                                         color:#0f2340; border-bottom:1px solid #e8eef5;">
                                Status</td>
                              <td style="padding:11px 20px; font-size:12px; color:#1a1a1a;
                                         border-bottom:1px solid #e8eef5;">%s</td>
                            </tr>
                            <!-- Row 5 -->
                            <tr style="background:#ffffff;">
                              <td style="padding:11px 20px; font-size:12px; font-weight:700;
                                         color:#0f2340;">Date Generated</td>
                              <td style="padding:11px 20px; font-size:12px; color:#1a1a1a;">
                                %s</td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- ── ATTACHMENT NOTICE ─────────────────────────────── -->
                      <tr>
                        <td style="padding:0 40px 26px;">
                          <table width="100%%" cellpadding="0" cellspacing="0"
                                 style="background:#eef3f8; border-left:4px solid #0f2340;
                                        border-radius:6px; overflow:hidden;">
                            <tr>
                              <td style="padding:13px 18px;">
                                <p style="margin:0 0 4px; font-size:13px; font-weight:700;
                                          color:#0f2340;">
                                  &#128206;&nbsp; PDF Report Attached
                                </p>
                                <p style="margin:0; font-size:12px; color:#4a5568; line-height:1.6;">
                                  The complete Preliminary Lead Assessment Report is attached as
                                  <strong>PreReport_%s.pdf</strong>. Please open the attachment
                                  to view the full report.
                                </p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- ── CONFIDENTIALITY NOTICE ───────────────────────── -->
                      <tr>
                        <td style="padding:0 40px 28px;">
                          <table width="100%%" cellpadding="0" cellspacing="0"
                                 style="background:#fff8ec; border:1px solid #f0d9a8;
                                        border-left:4px solid #c8972b; border-radius:6px;
                                        overflow:hidden;">
                            <tr>
                              <td style="padding:12px 18px;">
                                <p style="margin:0 0 4px; font-size:11px; font-weight:700;
                                          color:#7a5a0e; text-transform:uppercase;
                                          letter-spacing:0.5px;">
                                  Confidential
                                </p>
                                <p style="margin:0; font-size:11.5px; color:#6b5a2e;
                                          line-height:1.65;">
                                  This email and its attachments contain confidential information
                                  intended solely for the named recipient. If you have received
                                  this in error, please notify the sender immediately and delete
                                  this email without forwarding or copying it.
                                </p>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>

                      <!-- ── SIGN-OFF ──────────────────────────────────────── -->
                          <tr>
                            <td style="padding:0 40px 34px;">
                              <p style="margin:0 0 2px; font-size:14px; font-weight:700;
                                        color:#0f2340;">Warm regards,</p>
                              <p style="margin:0 0 2px; font-size:14px; font-weight:600;
                                        color:#0f2340;">TBCPL Operations Team</p>
                              <p style="margin:0 0 6px; font-size:14px; font-weight:600;
                                        color:#0f2340;">TBCPL</p>
                              <p style="margin:0; font-size:12px; color:#4a5568;">
                                <a href="https://tbcpl.co.in"
                                   target="_blank"
                                   rel="noopener noreferrer"
                                   style="color:#0f2340; text-decoration:none; display:inline-flex;
                                          align-items:center; gap:4px;">
                                  &#127760;&nbsp;tbcpl.co.in
                                </a>
                              </p>
                            </td>
                          </tr>

                      <!-- ── GOLD BAR BOTTOM ──────────────────────────────── -->
                      <tr>
                        <td style="background:#c8972b; height:3px;
                                   font-size:0; line-height:0;">&nbsp;</td>
                      </tr>

                      <!-- ── FOOTER ────────────────────────────────────────── -->
                      <tr>
                        <td style="background:#0f2340; padding:16px 40px; text-align:center;">
                          <p style="margin:0; font-size:11px; color:#a8c4dc;">
                            &copy; %d True Buddy Consulting Pvt. Ltd.
                            &nbsp;|&nbsp; Confidential &amp; Proprietary
                          </p>
                        </td>
                      </tr>

                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(
                escapeHtml(request.getToName()),        // Dear {name}
                escapeHtml(request.getCaseTitle()),      // for {caseTitle}  ← NEW
                notesBlock,                              // optional notes
                escapeHtml(preReport.getReportId()),     // Report ID
                escapeHtml(clientName),                  // Client Name
                leadType,                                // Lead Type
                status,                                  // Status
                dateGenerated,                           // Date Generated
                escapeHtml(preReport.getReportId()),     // PDF filename
                currentYear                              // Footer year
        );
    }

    // ─── Utilities ────────────────────────────────────────────────────────────

    private String toTitleCase(String value) {
        if (value == null || value.isBlank()) return "N/A";
        return value.charAt(0) + value.substring(1).toLowerCase().replace("_", " ");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}