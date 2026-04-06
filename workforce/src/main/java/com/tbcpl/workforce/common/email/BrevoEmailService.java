package com.tbcpl.workforce.common.email;

import com.tbcpl.workforce.common.exception.EmailDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BrevoEmailService {

    private final JavaMailSender mailSender;
    private final String senderEmail;
    private final String senderName;
    private final String replyTo;

    public BrevoEmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from}") String senderEmail,
            @Value("${app.mail.from-name}") String senderName,
            @Value("${app.mail.reply-to}") String replyTo
    ) {
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.replyTo = replyTo;
    }

    /**
     * Sends a plain transactional email (no attachment).
     * Used for: OTP, verification, password reset, etc.
     */
    public void sendEmail(String toEmail, String toName, String subject, String htmlBody) {
        log.info("Sending transactional email to: {} | Subject: {}", toEmail, subject);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail, senderName);
            helper.setReplyTo(replyTo);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", toEmail);
        } catch (Exception ex) {
            log.error("Failed to send email to {}: {}", toEmail, ex.getMessage(), ex);
            throw new EmailDeliveryException("Failed to send email: " + ex.getMessage(), ex);
        }
    }

    /**
     * Sends a transactional email WITH a PDF attachment.
     * Used for: Pre-reports, Final reports, Proposals, Letters of Authority, etc.
     */
    public void sendReportEmail(
            String toEmail,
            String toName,
            String subject,
            String htmlBody,
            byte[] pdfBytes,
            String pdfFileName
    ) {
        log.info("Sending report email to: {} | Attachment: {}", toEmail, pdfFileName);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, senderName);
            helper.setReplyTo(replyTo);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            helper.addAttachment(pdfFileName, new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            log.info("Report email sent successfully to: {}", toEmail);
        } catch (MessagingException ex) {
            log.error("Messaging error sending report to {}: {}", toEmail, ex.getMessage(), ex);
            throw new EmailDeliveryException("Failed to send report email: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Unexpected error sending report to {}: {}", toEmail, ex.getMessage(), ex);
            throw new EmailDeliveryException("Email delivery failed: " + ex.getMessage(), ex);
        }
    }
}