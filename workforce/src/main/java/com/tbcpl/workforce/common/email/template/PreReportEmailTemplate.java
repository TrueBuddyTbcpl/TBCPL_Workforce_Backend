// com.tbcpl.workforce.common.email.template.PreReportEmailTemplate.java
package com.tbcpl.workforce.common.email.template;

import com.tbcpl.workforce.common.dto.ReportEmailRequestDto;
import org.springframework.stereotype.Component;

@Component
public class PreReportEmailTemplate implements ReportEmailTemplate {

    @Override
    public String buildSubject(ReportEmailRequestDto request) {
        return "Preliminary Lead Assessment Report — %s | Ref: %s"
                .formatted(request.getClientName(), request.getCaseReference());
    }

    @Override
    public String buildHtmlBody(ReportEmailRequestDto request) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Preliminary Lead Assessment Report</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f4f6f9;font-family:'Segoe UI',Arial,sans-serif;">

                  <!-- WRAPPER -->
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f6f9;padding:40px 0;">
                    <tr>
                      <td align="center">
                        <table width="620" cellpadding="0" cellspacing="0"
                               style="background:#ffffff;border-radius:10px;overflow:hidden;
                                      box-shadow:0 4px 20px rgba(0,0,0,0.08);">

                          <!-- HEADER BANNER -->
                          <tr>
                            <td style="background:linear-gradient(135deg,#1a2e4a 0%%,#243d5f 100%%);
                                       padding:32px 40px;text-align:center;">
                              <p style="margin:0 0 6px 0;font-size:11px;letter-spacing:3px;
                                        color:#7eb8f7;text-transform:uppercase;font-weight:600;">
                                True Buddy Consulting Pvt. Ltd.
                              </p>
                              <h1 style="margin:0;font-size:22px;color:#ffffff;font-weight:700;
                                         letter-spacing:0.5px;">
                                Preliminary Lead Assessment Report
                              </h1>
                              <p style="margin:10px 0 0 0;font-size:13px;color:#aac8e8;">
                                Confidential Intelligence Document
                              </p>
                            </td>
                          </tr>

                          <!-- GREETING -->
                          <tr>
                            <td style="padding:36px 40px 0 40px;">
                              <p style="margin:0;font-size:15px;color:#2c3e50;line-height:1.6;">
                                Dear <strong>%s</strong>,
                              </p>
                              <p style="margin:14px 0 0 0;font-size:15px;color:#4a5568;line-height:1.8;">
                                Please find enclosed the <strong>Preliminary Lead Assessment Report</strong>
                                prepared by our intelligence team. This report provides an initial
                                desk-based evaluation of the assigned lead.
                              </p>
                            </td>
                          </tr>

                          <!-- REPORT DETAIL CARD -->
                          <tr>
                            <td style="padding:28px 40px;">
                              <table width="100%%" cellpadding="0" cellspacing="0"
                                     style="background:#f0f5ff;border-left:4px solid #2563eb;
                                            border-radius:6px;padding:20px 24px;">
                                <tr>
                                  <td>
                                    <p style="margin:0 0 12px 0;font-size:12px;font-weight:700;
                                              color:#2563eb;letter-spacing:2px;text-transform:uppercase;">
                                      Report Details
                                    </p>
                                    <table width="100%%" cellpadding="6" cellspacing="0"
                                           style="font-size:14px;color:#374151;">
                                      <tr>
                                        <td style="width:40%%;color:#6b7280;font-weight:600;">Report Type</td>
                                        <td style="color:#1a2e4a;font-weight:600;">
                                          Preliminary Lead Assessment
                                        </td>
                                      </tr>
                                      <tr style="background:#e8eeff;border-radius:4px;">
                                        <td style="color:#6b7280;font-weight:600;">Case Reference</td>
                                        <td style="color:#1a2e4a;font-weight:600;">%s</td>
                                      </tr>
                                      <tr>
                                        <td style="color:#6b7280;font-weight:600;">Client / Subject</td>
                                        <td style="color:#1a2e4a;font-weight:600;">%s</td>
                                      </tr>
                                      <tr style="background:#e8eeff;">
                                        <td style="color:#6b7280;font-weight:600;">Prepared By</td>
                                        <td style="color:#1a2e4a;font-weight:600;">%s</td>
                                      </tr>
                                      <tr>
                                        <td style="color:#6b7280;font-weight:600;">Report Date</td>
                                        <td style="color:#1a2e4a;font-weight:600;">%s</td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>

                          <!-- ATTACHMENT NOTICE -->
                          <tr>
                            <td style="padding:0 40px 28px 40px;">
                              <table width="100%%" cellpadding="0" cellspacing="0"
                                     style="background:#fff7ed;border:1px solid #fed7aa;
                                            border-radius:6px;padding:16px 20px;">
                                <tr>
                                  <td style="font-size:13px;color:#92400e;">
                                    📎 &nbsp;
                                    <strong>Attachment:</strong> The full report is attached as a
                                    PDF document. Please ensure you handle this document in
                                    accordance with your confidentiality obligations.
                                  </td>
                                </tr>
                              </table>
                            </td>
                          </tr>

                          <!-- DISCLAIMER -->
                          <tr>
                            <td style="padding:0 40px 28px 40px;">
                              <p style="margin:0;font-size:12px;color:#9ca3af;line-height:1.7;
                                        border-top:1px solid #e5e7eb;padding-top:20px;">
                                <strong style="color:#6b7280;">Confidentiality Notice:</strong>
                                This email and its attachments are intended solely for the named
                                recipient. The contents are confidential and may be legally privileged.
                                Unauthorized use, disclosure, or distribution is strictly prohibited.
                                If you have received this in error, please notify us immediately and
                                delete all copies.
                              </p>
                            </td>
                          </tr>

                          <!-- FOOTER -->
                          <tr>
                            <td style="background:#1a2e4a;padding:22px 40px;text-align:center;">
                              <p style="margin:0;font-size:12px;color:#7eb8f7;font-weight:600;">
                                True Buddy Consulting Pvt. Ltd.
                              </p>
                              <p style="margin:6px 0 0 0;font-size:11px;color:#4a6a8a;">
                                This is an automated message — please do not reply.
                              </p>
                              <p style="margin:6px 0 0 0;font-size:11px;color:#4a6a8a;">
                                © 2026 True Buddy Consulting Pvt. Ltd. All rights reserved.
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
                request.getToName(),
                request.getCaseReference(),
                request.getClientName(),
                request.getPreparedBy(),
                request.getReportDate()
        );
    }
}