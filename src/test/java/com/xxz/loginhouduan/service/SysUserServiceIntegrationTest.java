package com.xxz.loginhouduan.service;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.req.SysUserLoginReq;
import com.xxz.loginhouduan.req.SysUserSaveReq;
import com.xxz.loginhouduan.resp.SysUserLoginResp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests using H2 in-memory database.
 */
@SpringBootTest
@ActiveProfiles("test")
public class SysUserServiceIntegrationTest {

    @Autowired
    private SysUserService sysUserService;

    @BeforeEach
    public void setupUser() {
        // 动态生成唯一用户名
        String username = "testuser" + UUID.randomUUID().toString();

        // 清理已存在的用户
        List<SysUserEntity> existingUser = sysUserService.searchUsersByName(username);
        if (!existingUser.isEmpty()) {
            // 删除已存在的用户
            sysUserService.deleteByLoginName(username);
        }
    }

    @Test
    public void testRegisterAndLogin() {
        // 注册新用户
        SysUserSaveReq saveReq = new SysUserSaveReq();
        saveReq.setLoginName("testuser123");
        saveReq.setPassword("123456");
        sysUserService.register(saveReq);

        // 登录操作
        SysUserLoginReq loginReq = new SysUserLoginReq();
        loginReq.setLoginName("testuser123");
        loginReq.setPassword("123456");

        SysUserLoginResp loginResp = sysUserService.login(loginReq);
        assertNotNull(loginResp);
        assertEquals("testuser123", loginResp.getLoginName());
    }

    @Test
    public void testSearchUsers() {
        // 注册一个新用户
        SysUserSaveReq req = new SysUserSaveReq();
        req.setLoginName("searchme" + UUID.randomUUID().toString());
        req.setPassword("abc123");
        sysUserService.register(req);

        // 查询用户
        List<SysUserEntity> results = sysUserService.searchUsersByName("search");
        assertFalse(results.isEmpty());
        assertTrue(results.get(0).getLoginName().contains("search"));
    }
}
