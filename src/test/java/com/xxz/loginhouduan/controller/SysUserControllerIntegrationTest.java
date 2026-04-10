package com.xxz.loginhouduan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.req.SysUserLoginReq;
import com.xxz.loginhouduan.req.SysUserSaveReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for SysUserController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // 使用 application-test.yml 配置
public class SysUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 每个测试前确保用户 john 存在，避免空数据导致失败。
     */
    @BeforeEach
    public void setup() {
        SysUserEntity exist = sysUserMapper.findByEmail("john@example.com");
        if (exist == null) {
            SysUserEntity user = new SysUserEntity();
            user.setLoginName("john");
            user.setEmail("john@example.com");
            user.setPassword("pw123");
            sysUserMapper.insert(user);
        }
    }

    @Test
    public void testRegisterAndLogin() throws Exception {
        SysUserSaveReq reg = new SysUserSaveReq();
        reg.setLoginName("newuser");
        reg.setPassword("test123");

        mockMvc.perform(post("/sys-user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        SysUserLoginReq login = new SysUserLoginReq();
        login.setLoginName("newuser");
        login.setPassword("test123");

        mockMvc.perform(post("/sys-user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.loginName").value("newuser"));
    }

    @Test
    public void testSearchUserAndGetId() throws Exception {
        mockMvc.perform(get("/sys-user/search").param("name", "john"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("john")));

        mockMvc.perform(get("/sys-user/get-id").param("loginName", "john"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUser() throws Exception {
        String userId = mockMvc.perform(get("/sys-user/get-id").param("loginName", "john"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(delete("/sys-user/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User deleted")));
    }
}
