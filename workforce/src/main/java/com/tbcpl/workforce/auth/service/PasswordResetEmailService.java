package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.entity.Employee;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetEmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")      private String fromEmail;
    @Value("${app.mail.from-name}") private String fromName;
    @Value("${app.mail.reply-to}")  private String replyTo;
    @Value("${app.base-url}")       private String baseUrl;

    // ── Reset password link email ─────────────────────────────────────────────
    @Async
    public void sendResetEmail(Employee employee, String token) {
        String resetUrl = baseUrl + "/reset-password?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            try {
                helper.setFrom(new InternetAddress(fromEmail, fromName));
            } catch (java.io.UnsupportedEncodingException e) {
                helper.setFrom(fromEmail);
            }
            helper.setReplyTo(replyTo);
            helper.setTo(employee.getEmail());
            helper.setSubject("Reset Your Password — TBCPL WorkForce");
            helper.setText(buildResetHtml(employee, resetUrl), true);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", employee.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send reset email to: {}", employee.getEmail(), e);
        }
    }

    // ── 7-day expiry warning email ────────────────────────────────────────────
    @Async
    public void sendExpiryWarningEmail(Employee employee) {
        long daysLeft = employee.getDaysUntilPasswordExpiry();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            try {
                helper.setFrom(new InternetAddress(fromEmail, fromName));
            } catch (java.io.UnsupportedEncodingException e) {
                helper.setFrom(fromEmail);
            }
            helper.setReplyTo(replyTo);
            helper.setTo(employee.getEmail());
            helper.setSubject("⚠️ Your Password Expires in " + daysLeft + " Days — TBCPL WorkForce");
            helper.setText(buildExpiryWarningHtml(employee, daysLeft), true);
            mailSender.send(message);
            log.info("Expiry warning email sent to: {} ({} days left)",
                    employee.getEmail(), daysLeft);
        } catch (MessagingException e) {
            log.error("Failed to send expiry warning to: {}", employee.getEmail(), e);
        }
    }

    // ── HTML Templates ────────────────────────────────────────────────────────

    private String buildResetHtml(Employee employee, String resetUrl) {
        return """
            <!DOCTYPE html><html><head><meta charset="UTF-8"/></head>
            <body style="margin:0;padding:0;background:#f4f6f9;font-family:Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding:40px 20px;">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background:#fff;border-radius:12px;overflow:hidden;
                                box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                    <tr><td style="background:linear-gradient(135deg,#0f1923,#1a2235);
                                   padding:36px 40px;text-align:center;">
                      <p style="color:#4BA3D4;font-size:26px;font-weight:900;
                                letter-spacing:4px;margin:0;">TRUE BUDDY</p>
                      <div style="background:#4BA3D4;display:inline-block;
                                  padding:4px 20px;border-radius:6px;margin-top:6px;">
                        <p style="color:#fff;font-size:13px;font-weight:bold;
                                  font-style:italic;letter-spacing:3px;margin:0;">Consulting</p>
                      </div>
                    </td></tr>
                    <tr><td style="padding:40px;">
                      <h2 style="color:#1a2235;font-size:22px;margin:0 0 8px;">
                        Reset Your Password
                      </h2>
                      <p style="color:#666;font-size:15px;line-height:1.6;margin:0 0 24px;">
                        Hi <strong>%s</strong>,<br/>
                        We received a request to reset your TBCPL WorkForce password.
                        Click the button below to set a new password.
                      </p>
                      <div style="text-align:center;margin:32px 0;">
                        <a href="%s"
                           style="background:linear-gradient(135deg,#4BA3D4,#2980b9);
                                  color:#ffffff;text-decoration:none;padding:14px 40px;
                                  border-radius:8px;font-size:16px;font-weight:bold;
                                  display:inline-block;">
                          🔒&nbsp; Reset My Password
                        </a>
                      </div>
                      <p style="color:#999;font-size:13px;text-align:center;margin:0 0 24px;">
                        This link expires in <strong>1 hour</strong>.
                        If you didn't request this, ignore this email.
                      </p>
                      <div style="background:#f8f9fa;border-radius:8px;padding:16px;
                                  border-left:4px solid #4BA3D4;">
                        <p style="color:#666;font-size:12px;margin:0 0 6px;">
                          If the button doesn't work, copy and paste this link:
                        </p>
                        <p style="color:#4BA3D4;font-size:11px;margin:0;word-break:break-all;">%s</p>
                      </div>
                    </td></tr>
                    <tr><td style="background:#f8f9fa;padding:24px 40px;
                                   border-top:1px solid #eee;text-align:center;">
                      <p style="color:#999;font-size:12px;margin:0 0 4px;">
                        This is an automated message — please do not reply.
                      </p>
                      <p style="color:#bbb;font-size:11px;margin:0;">
                        © 2026 True Buddy Consulting Pvt. Ltd.
                      </p>
                    </td></tr>
                  </table>
                </td></tr>
              </table>
            </body></html>
            """.formatted(employee.getFirstName(), resetUrl, resetUrl);
    }

    private String buildExpiryWarningHtml(Employee employee, long daysLeft) {
        String changeUrl = baseUrl + "/change-password";
        return """
            <!DOCTYPE html><html><head><meta charset="UTF-8"/></head>
            <body style="margin:0;padding:0;background:#f4f6f9;font-family:Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding:40px 20px;">
                  <table width="600" cellpadding="0" cellspacing="0"
                         style="background:#fff;border-radius:12px;overflow:hidden;
                                box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                    <tr><td style="background:linear-gradient(135deg,#0f1923,#1a2235);
                                   padding:36px 40px;text-align:center;">
                      <p style="color:#4BA3D4;font-size:26px;font-weight:900;
                                letter-spacing:4px;margin:0;">TRUE BUDDY</p>
                    </td></tr>
                    <tr><td style="padding:40px;">
                      <div style="background:#fff3cd;border:1px solid #ffc107;
                                  border-radius:8px;padding:16px;margin-bottom:24px;">
                        <p style="color:#856404;font-size:16px;font-weight:bold;margin:0;">
                          ⚠️ Your password expires in %d day%s
                        </p>
                      </div>
                      <p style="color:#666;font-size:15px;line-height:1.6;margin:0 0 24px;">
                        Hi <strong>%s</strong>,<br/>
                        Your TBCPL WorkForce password will expire soon.
                        Please change it now to avoid losing access.
                      </p>
                      <div style="text-align:center;margin:32px 0;">
                        <a href="%s"
                           style="background:linear-gradient(135deg,#f39c12,#e67e22);
                                  color:#ffffff;text-decoration:none;padding:14px 40px;
                                  border-radius:8px;font-size:16px;font-weight:bold;
                                  display:inline-block;">
                          🔑&nbsp; Change My Password
                        </a>
                      </div>
                    </td></tr>
                    <tr><td style="background:#f8f9fa;padding:24px 40px;
                                   border-top:1px solid #eee;text-align:center;">
                      <p style="color:#bbb;font-size:11px;margin:0;">
                        © 2026 True Buddy Consulting Pvt. Ltd.
                      </p>
                    </td></tr>
                  </table>
                </td></tr>
              </table>
            </body></html>
            """.formatted(daysLeft, daysLeft == 1 ? "" : "s",
                employee.getFirstName(), changeUrl);
    }
}
