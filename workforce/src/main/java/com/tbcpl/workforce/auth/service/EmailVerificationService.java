package com.tbcpl.workforce.auth.service;

import com.tbcpl.workforce.auth.entity.Employee;
import com.tbcpl.workforce.auth.entity.EmailVerificationToken;
import com.tbcpl.workforce.auth.repository.EmailVerificationTokenRepository;
import com.tbcpl.workforce.common.constants.ApiEndpoints;
import com.tbcpl.workforce.common.exception.ResourceNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final JavaMailSender                   mailSender;
    private final EmailVerificationTokenRepository tokenRepository;
    private final VerificationTokenService         verificationTokenService; // ← INJECTED

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.mail.reply-to}")
    private String replyTo;

    @Value("${app.base-url}")
    private String baseUrl;

    // ─────────────────────────────────────────────────────────────────────────
    // Send verification email — async, calls separate bean for DB work
    // ─────────────────────────────────────────────────────────────────────────
    @Async
    public void sendVerificationEmail(Employee employee) {
        log.info("📧 [ASYNC] sendVerificationEmail started for: {}", employee.getEmail());
        try {
            // FIX: Call through separate Spring bean — proxy applies @Transactional correctly
            String token = verificationTokenService.createAndSaveToken(employee);
            String verificationUrl = baseUrl + ApiEndpoints.AUTH_BASE + ApiEndpoints.AUTH_VERIFY_EMAIL + "?token=" + token;
            log.info("📧 [ASYNC] Verification URL: {}", verificationUrl);
            sendVerificationMail(employee, verificationUrl);
            log.info("✅ Verification email sent to: {}", employee.getEmail());
        } catch (Exception e) {
            log.error("❌ Failed to send verification email to: {}", employee.getEmail(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Resend — called by ADMIN or resend endpoint
    // ─────────────────────────────────────────────────────────────────────────
    @Async
    public void resendVerificationEmail(Employee employee) {
        if (Boolean.TRUE.equals(employee.getEmailVerified())) {
            log.info("Email already verified for: {}", employee.getEmail());
            return;
        }
        sendVerificationEmail(employee);
        log.info("Resend triggered for: {}", employee.getEmail());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Verify token — called when employee clicks the link
    // ─────────────────────────────────────────────────────────────────────────
    @Transactional
    public void verifyToken(String token, EmployeeService employeeService) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Verification link is invalid or has already been used."));

        if (Boolean.TRUE.equals(verificationToken.getUsed())) {
            throw new ResourceNotFoundException(
                    "Verification link has already been used.");
        }

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException(
                    "Verification link has expired. Please request a new one.");
        }

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        employeeService.markEmailAsVerified(
                verificationToken.getEmployee().getId());

        log.info("✅ Email verified for employee: {}",
                verificationToken.getEmployee().getEmpId());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Build and dispatch MIME email via Brevo SMTP
    // ─────────────────────────────────────────────────────────────────────────
    private void sendVerificationMail(Employee employee, String verificationUrl)
            throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        message.addHeader("X-Mailin-Track-Click", "0");
        message.addHeader("X-Mailin-Track-Open", "0");
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(new InternetAddress(fromEmail, fromName));
        } catch (java.io.UnsupportedEncodingException e) {
            log.error("Invalid fromName encoding, using plain address", e);
            helper.setFrom(fromEmail);
        }

        helper.setReplyTo(replyTo);
        helper.setTo(employee.getEmail());
        helper.setSubject("Verify Your Email — TBCPL WorkForce");
        helper.setText(buildEmailHtml(employee, verificationUrl), true);

        mailSender.send(message);
        log.info("✅ Email dispatched via Brevo SMTP to: {}", employee.getEmail());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HTML email template
    // ─────────────────────────────────────────────────────────────────────────
    private String buildEmailHtml(Employee employee, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
            </head>
            <body style="margin:0;padding:0;background:#f4f6f9;font-family:Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" style="padding:40px 20px;">
                    <table width="600" cellpadding="0" cellspacing="0"
                           style="background:#ffffff;border-radius:12px;overflow:hidden;
                                  box-shadow:0 4px 20px rgba(0,0,0,0.08);">
                      <tr>
                        <td style="background:linear-gradient(135deg,#0f1923,#1a2235);
                                   padding:36px 40px;text-align:center;">
                          <p style="color:#4BA3D4;font-size:26px;font-weight:900;
                                    letter-spacing:4px;margin:0;">TRUE BUDDY</p>
                          <div style="background:#4BA3D4;display:inline-block;
                                      padding:4px 20px;border-radius:6px;margin-top:6px;">
                            <p style="color:#fff;font-size:13px;font-weight:bold;
                                      font-style:italic;letter-spacing:3px;margin:0;">
                              Consulting
                            </p>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td style="padding:40px;">
                          <h2 style="color:#1a2235;font-size:22px;margin:0 0 8px;">
                            Verify Your Email Address
                          </h2>
                          <p style="color:#666;font-size:15px;line-height:1.6;margin:0 0 24px;">
                            Hi <strong>%s</strong>, welcome to TBCPL WorkForce!<br/>
                            Please verify your email address to activate your account.
                          </p>
                          <div style="text-align:center;margin:32px 0;">
                            <a href="%s"
                               style="background:linear-gradient(135deg,#4BA3D4,#2980b9);
                                      color:#ffffff;text-decoration:none;padding:14px 40px;
                                      border-radius:8px;font-size:16px;font-weight:bold;
                                      display:inline-block;">
                              ✉&nbsp; Verify My Email
                            </a>
                          </div>
                          <p style="color:#999;font-size:13px;text-align:center;margin:0 0 24px;">
                            This link expires in <strong>24 hours</strong>
                          </p>
                          <div style="background:#f8f9fa;border-radius:8px;padding:16px;
                                      border-left:4px solid #4BA3D4;">
                            <p style="color:#666;font-size:12px;margin:0 0 6px;">
                              If the button doesn't work, copy and paste this link:
                            </p>
                            <p style="color:#4BA3D4;font-size:11px;margin:0;
                                      word-break:break-all;">%s</p>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td style="background:#f8f9fa;padding:24px 40px;
                                   border-top:1px solid #eee;text-align:center;">
                          <p style="color:#999;font-size:12px;margin:0 0 4px;">
                            This is an automated message — please do not reply.
                          </p>
                          <p style="color:#bbb;font-size:11px;margin:0;">
                            © 2026 True Buddy Consulting Pvt. Ltd. · tbcontrolcenter.com
                          </p>
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(employee.getFirstName(), verificationUrl, verificationUrl);
    }
}
