package com.xxz.loginhouduan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @MockBean
    private EmailService emailService;

    @BeforeEach
    public void setupUser() {
        SysUserEntity user = sysUserMapper.findByEmail("validuser@example.com");
        if (user == null) {
            user = new SysUserEntity();
            user.setLoginName("testuser");
            user.setEmail("validuser@example.com");
            user.setPassword("password123");
            user.setResetToken("valid-reset-token");
            user.setResetTokenExpire(LocalDateTime.now().plusHours(1));
            sysUserMapper.insert(user);
        }
    }

    @Test
    public void testForgotPassword_validEmail() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("email", "validuser@example.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("A password reset link has been sent to your email."));

        verify(emailService, times(1)).sendEmail(Mockito.eq("validuser@example.com"), anyString());
    }

    @Test
    public void testForgotPassword_invalidEmail() throws Exception {
        String emailJson = "{\"email\":\"nonexistent@example.com\"}";

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("This email is not registered."));

        verify(emailService, times(0)).sendEmail(anyString(), anyString());
    }

    @Test
    public void testResetPassword_validToken_shouldSucceed() throws Exception {
        // 确保 token 有效且未过期
        SysUserEntity user = sysUserMapper.findByEmail("validuser@example.com");
        String token = "valid-reset-token";
        user.setResetToken(token);
        user.setResetTokenExpire(LocalDateTime.now().plusHours(1));
        sysUserMapper.updateResetToken(user.getEmail(), token, user.getResetTokenExpire().toString());

        Map<String, String> req = new HashMap<>();
        req.put("token", token);
        req.put("newPassword", "newPassword123");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().string("Your password has been successfully reset."));
    }

    @Test
    public void testResetPassword_invalidToken_shouldFail() throws Exception {
        Map<String, String> req = new HashMap<>();
        req.put("token", "invalid-token");
        req.put("newPassword", "irrelevant");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid or expired reset link."));
    }

    @Test
    public void testResetPassword_expiredToken_shouldFail() throws Exception {
        SysUserEntity user = sysUserMapper.findByEmail("validuser@example.com");
        String token = "expired-token";
        user.setResetToken(token);
        user.setResetTokenExpire(LocalDateTime.now().minusMinutes(10));
        sysUserMapper.updateResetToken(user.getEmail(), token, user.getResetTokenExpire().toString());

        Map<String, String> req = new HashMap<>();
        req.put("token", token);
        req.put("newPassword", "irrelevant");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Reset link has expired. Please request a new one."));
    }

}