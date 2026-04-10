package com.xxz.loginhouduan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() throws Exception {
        mailSender = Mockito.mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);

        // 注入 spring.mail.username 字段
        Field usernameField = EmailService.class.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(emailService, "test@example.com");
    }

    @Test
    void testSendEmail_success() {
        // 创建模拟的 MimeMessage
        MimeMessage mockMimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        assertDoesNotThrow(() ->
                emailService.sendEmail("user@example.com", "http://example.com/reset"));
    }

    @Test
    void testSendEmail_failure() throws Exception {
        MimeMessage mockMimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mockMimeMessage);

        // 模拟 mailSender.send() 抛出 MessagingException
        doAnswer(invocation -> {
            throw new MessagingException("Simulated failure");
        }).when(mailSender).send(any(MimeMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendEmail("user@example.com", "http://example.com/reset"));

        assertTrue(exception.getMessage().contains("Email sending failed"));
    }
}
