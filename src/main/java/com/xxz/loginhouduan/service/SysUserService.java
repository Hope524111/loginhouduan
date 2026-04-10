package com.xxz.loginhouduan.service;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.req.SysUserLoginReq;
import com.xxz.loginhouduan.req.SysUserSaveReq;
import com.xxz.loginhouduan.resp.SysUserLoginResp;
import java.util.List;
public interface SysUserService {
    void register(SysUserSaveReq req);

    SysUserLoginResp login(SysUserLoginReq req);

    // Methods for adding and deleting users
    void deleteUserById(Long id);
    List<SysUserEntity> searchUsersByName(String name);

    void deleteByLoginName(String username);
}
