package com.xxz.loginhouduan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.req.SysUserLoginReq;
import com.xxz.loginhouduan.req.SysUserSaveReq;
import com.xxz.loginhouduan.resp.SysUserLoginResp;
import com.xxz.loginhouduan.service.SysUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SysUserController.class)
public class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysUserService sysUserService;

    @MockBean
    private SysUserMapper sysUserMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private SysUserEntity user;

    @BeforeEach
    public void setup() {
        user = new SysUserEntity();
        user.setId(1L);
        user.setLoginName("alice");
        user.setEmail("alice@example.com");
    }

    @Test
    public void testRegister_success() throws Exception {
        SysUserSaveReq req = new SysUserSaveReq();
        req.setLoginName("testuser");
        req.setPassword("12345");

        mockMvc.perform(post("/sys-user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testLogin_success() throws Exception {
        SysUserLoginReq req = new SysUserLoginReq();
        req.setLoginName("alice");
        req.setPassword("12345");

        SysUserLoginResp loginResp = new SysUserLoginResp();
        loginResp.setLoginName("alice");

        Mockito.when(sysUserService.login(any(SysUserLoginReq.class))).thenReturn(loginResp);

        mockMvc.perform(post("/sys-user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.loginName").value("alice"));
    }

    @Test
    public void testDeleteUser_success() throws Exception {
        mockMvc.perform(delete("/sys-user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    public void testSearchUsersByName_success() throws Exception {
        Mockito.when(sysUserService.searchUsersByName("alice")).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/sys-user/search").param("name", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loginName").value("alice"));
    }

    @Test
    public void testGetUserIdByLoginName_success() throws Exception {
        Mockito.when(sysUserService.searchUsersByName("alice")).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/sys-user/get-id").param("loginName", "alice"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void testGetAllUsers_success() throws Exception {
        Mockito.when(sysUserMapper.findAll()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/sys-user/Admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loginName").value("alice"));
    }

    @Test
    public void testRegister_shortPassword_shouldFail() throws Exception {
        SysUserSaveReq req = new SysUserSaveReq();
        req.setLoginName("testuser");
        req.setPassword("1");

        mockMvc.perform(post("/sys-user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError()) // 捕获 500 错误
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Password length")));
    }

    @Test
    public void testRegister_invalidPassword_shouldFail() throws Exception {
        SysUserSaveReq req = new SysUserSaveReq();
        req.setLoginName("test");
        req.setEmail("test@example.com");
        req.setPassword("1"); // too short

        mockMvc.perform(post("/sys-user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Password length should be between 2 and 9 characters")));
    }


    @Test
    public void testDeleteUser_userNotFound_shouldFail() throws Exception {
        Mockito.doThrow(new RuntimeException("User not found")).when(sysUserService).deleteUserById(999L);

        mockMvc.perform(delete("/sys-user/999"))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string("Error deleting user: User not found"));
    }

    @Test
    public void testLogin_invalidCredentials_shouldFail() throws Exception {
        SysUserLoginReq req = new SysUserLoginReq();
        req.setLoginName("wrong");
        req.setPassword("wrong");

        Mockito.when(sysUserService.login(any())).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/sys-user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    public void testGetUserId_userNotFound_shouldReturn404() throws Exception {
        Mockito.when(sysUserService.searchUsersByName("ghost")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/sys-user/get-id").param("loginName", "ghost"))
                .andExpect(status().isNotFound());
    }


}
