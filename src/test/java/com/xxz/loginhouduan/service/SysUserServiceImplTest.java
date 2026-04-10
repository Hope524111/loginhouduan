package com.xxz.loginhouduan.service;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.req.SysUserLoginReq;
import com.xxz.loginhouduan.req.SysUserSaveReq;
import com.xxz.loginhouduan.resp.SysUserLoginResp;
import com.xxz.loginhouduan.service.impl.SysUserServiceImpl;
import com.xxz.loginhouduan.utils.SnowFlake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SysUserServiceImplTest {

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    @Mock
    private SysUserMapper sysUserMapper;

    @Mock
    private SnowFlake snowFlake;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        SysUserEntity mockUser = new SysUserEntity();
        mockUser.setLoginName("admin");
        mockUser.setPassword("123456");

        when(sysUserMapper.selectList(any())).thenReturn(Collections.singletonList(mockUser));

        SysUserLoginReq req = new SysUserLoginReq();
        req.setLoginName("admin");
        req.setPassword("123456");

        SysUserLoginResp resp = sysUserService.login(req);
        assertNotNull(resp);
        assertEquals("admin", resp.getLoginName());
    }

    @Test
    public void testLoginUserNotFound() {
        when(sysUserMapper.selectList(any())).thenReturn(Collections.emptyList());

        SysUserLoginReq req = new SysUserLoginReq();
        req.setLoginName("nouser");
        req.setPassword("123");

        assertThrows(RuntimeException.class, () -> sysUserService.login(req));
    }

    @Test
    public void testLoginWrongPassword() {
        SysUserEntity mockUser = new SysUserEntity();
        mockUser.setLoginName("admin");
        mockUser.setPassword("correct");

        when(sysUserMapper.selectList(any())).thenReturn(Collections.singletonList(mockUser));

        SysUserLoginReq req = new SysUserLoginReq();
        req.setLoginName("admin");
        req.setPassword("wrong");

        assertThrows(RuntimeException.class, () -> sysUserService.login(req));
    }

    @Test
    public void testRegisterSuccess() {
        SysUserSaveReq req = new SysUserSaveReq();
        req.setLoginName("newuser");
        req.setPassword("pw");

        when(sysUserMapper.selectList(any())).thenReturn(Collections.emptyList());
        when(snowFlake.nextId()).thenReturn(1001L);

        sysUserService.register(req);
        verify(sysUserMapper, times(1)).insert(any());
    }

    @Test
    public void testRegisterDuplicate() {
        SysUserEntity existing = new SysUserEntity();
        existing.setLoginName("dupe");

        when(sysUserMapper.selectList(any())).thenReturn(Collections.singletonList(existing));

        SysUserSaveReq req = new SysUserSaveReq();
        req.setLoginName("dupe");
        req.setPassword("pw");

        assertThrows(RuntimeException.class, () -> sysUserService.register(req));
    }

    @Test
    public void testDeleteUserById() {
        when(sysUserMapper.deleteById(1L)).thenReturn(1);
        assertDoesNotThrow(() -> sysUserService.deleteUserById(1L));
    }

    @Test
    public void testDeleteUserWithNullId() {
        assertThrows(RuntimeException.class, () -> sysUserService.deleteUserById(null));
    }

    @Test
    public void testDeleteUserNotFound() {
        when(sysUserMapper.deleteById(999L)).thenReturn(0);
        assertThrows(RuntimeException.class, () -> sysUserService.deleteUserById(999L));
    }
}
