package com.xxz.loginhouduan.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

@TableName("sys_user")  // Associates with the database table
public class SysUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String loginName;
    private String email;
    private String password;

    @TableField("reset_token") // Tells MyBatis-Plus to recognize the database field
    private String resetToken;

    @TableField("reset_token_expire")
    private LocalDateTime resetTokenExpire;

    @TableField("chat_history") // Tells MyBatis-Plus to recognize the database field
    private String chatHistory;  // Stores chat history in JSON format

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoginName() { return loginName; }
    public void setLoginName(String loginName) { this.loginName = loginName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getResetTokenExpire() { return resetTokenExpire; }
    public void setResetTokenExpire(LocalDateTime resetTokenExpire) { this.resetTokenExpire = resetTokenExpire; }

    public String getChatHistory() { return chatHistory; }
    public void setChatHistory(String chatHistory) { this.chatHistory = chatHistory; }

    // toString method
    @Override
    public String toString() {
        return "SysUserEntity{" +
                "id=" + id +
                ", loginName='" + loginName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", resetToken='" + resetToken + '\'' +
                ", resetTokenExpire=" + resetTokenExpire +
                ", chatHistory='" + chatHistory + '\'' +
                '}';
    }
}
