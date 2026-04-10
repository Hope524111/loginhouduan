package com.xxz.loginhouduan.utils;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.resp.SysUserLoginResp;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CopyUtilTest {

    // Test copying a single valid object
    @Test
    void testCopy_singleObject() {
        SysUserEntity user = new SysUserEntity();
        user.setId(1L);
        user.setLoginName("admin");
        user.setEmail("admin@example.com");

        SysUserLoginResp resp = CopyUtil.copy(user, SysUserLoginResp.class);

        assertNotNull(resp); // Ensure the result is not null
        assertEquals(user.getId(), resp.getId()); // Check ID is copied
        assertEquals(user.getLoginName(), resp.getLoginName()); // Check loginName is copied
    }

    // Test copying a null object should return null
    @Test
    void testCopy_nullSource() {
        SysUserLoginResp result = CopyUtil.copy(null, SysUserLoginResp.class);
        assertNull(result); // Should return null
    }

    // Test copying an empty list
    @Test
    void testCopyList_emptyList() {
        List<SysUserLoginResp> result = CopyUtil.copyList(new ArrayList<>(), SysUserLoginResp.class);
        assertNotNull(result); // Should not be null
        assertTrue(result.isEmpty()); // Should be an empty list
    }

    // Test copying a list of valid objects
    @Test
    void testCopyList_validList() {
        SysUserEntity user = new SysUserEntity();
        user.setId(2L);
        user.setLoginName("test");

        List<SysUserEntity> entityList = new ArrayList<>();
        entityList.add(user);

        List<SysUserLoginResp> result = CopyUtil.copyList(entityList, SysUserLoginResp.class);

        assertEquals(1, result.size()); // One item should be copied
        assertEquals("test", result.get(0).getLoginName()); // Check copied content
    }
}
