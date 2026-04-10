package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

/**
 * AuthController handles authentication-related operations
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private EmailService emailService;

    // Frontend domain configured in application.yml
    @Value("${app.frontend-domain}")
    private String frontendDomain;

    /**
     * Forgot Password - send reset link to user's email
     * @param request Request body containing user's email
     * @return Response message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        SysUserEntity user = sysUserMapper.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().body("This email is not registered.");
        }

        // Generate a unique token and set expiration time (1 hour later)
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(2);

        // Format expireTime into SQL DATETIME format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String expireTimeStr = expireTime.format(formatter);

        // Update user with reset token and expiration
        sysUserMapper.updateResetToken(email, resetToken, expireTimeStr);

        // Build reset password link
        String resetLink = frontendDomain + "/reset-password?token=" + resetToken;

        // Send reset email
        emailService.sendEmail(email, resetLink);

        return ResponseEntity.ok("A password reset link has been sent to your email.");
    }

    /**
     * Reset Password - user submits new password using token
     * @param request Request body containing token and new password
     * @return Response message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        // Find user by token
        SysUserEntity user = sysUserMapper.findByResetToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid or expired reset link.");
        }

        // Check token expiration
        LocalDateTime expireTime = user.getResetTokenExpire();
        if (expireTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Reset link has expired. Please request a new one.");
        }

        // Encrypt new password using MD5
        String encryptedPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());

        // Update password and clear reset token
        sysUserMapper.updatePassword(token, encryptedPassword);

        return ResponseEntity.ok("Your password has been successfully reset.");
    }
}
