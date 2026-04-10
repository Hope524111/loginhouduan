package com.xxz.loginhouduan.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * EmailService handles sending emails via JavaMailSender
 */
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send password reset email to user
     * @param toEmail recipient's email
     * @param resetLink full password reset link (already built)
     */
    public void sendEmail(String toEmail, String resetLink) {
        String subject = "🔐 Password Reset Request";

        // Email body with HTML formatting
        String body = "<div style='font-family: Arial, sans-serif; max-width: 600px; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>"
                + "<h2 style='color: #333;'>🔐 Hello,</h2>"
                + "<p>You are receiving this email because a <strong>password reset request</strong> was made for your account.</p>"
                + "<p>If you did not request this, please ignore this email.</p>"
                + "<p>Click the button below to securely reset your password:</p>"
                + "<a href='" + resetLink + "' style='display: inline-block; padding: 12px 24px; font-size: 16px; font-weight: bold; color: #fff; background-color: #007bff; text-decoration: none; border-radius: 5px;'>🔗 Reset Password</a>"
                + "<p>If the button does not work, you can also copy and paste the following link into your browser:</p>"
                + "<p style='background-color: #f4f4f4; padding: 10px; border-radius: 5px;'>" + resetLink + "</p>"
                + "<p><strong>Note:</strong> This link will expire in <strong>1 hour</strong>. Please complete the process as soon as possible.</p>"
                + "<br>"
                + "<p style='color: #999;'>— This is an automated email, please do not reply.</p>"
                + "</div>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(username);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // 'true' enables HTML formatting

            mailSender.send(message);
            System.out.println("✅ Password reset email sent successfully to " + toEmail);

        } catch (MessagingException e) {
            System.err.println("❌ Failed to send password reset email to " + toEmail);
            e.printStackTrace();
            throw new RuntimeException("Email sending failed: " + e.getMessage());
        }
    }
}
