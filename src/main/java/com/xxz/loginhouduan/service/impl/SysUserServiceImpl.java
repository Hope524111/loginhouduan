package com.xxz.loginhouduan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.req.SysUserLoginReq;
import com.xxz.loginhouduan.req.SysUserSaveReq;
import com.xxz.loginhouduan.resp.SysUserLoginResp;
import com.xxz.loginhouduan.service.SysUserService;
import com.xxz.loginhouduan.utils.CopyUtil;
import com.xxz.loginhouduan.utils.SnowFlake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUserEntity> implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Autowired
    private SnowFlake snowFlake;

    @Override
    public void register(SysUserSaveReq req)  {
        SysUserEntity user = CopyUtil.copy(req, SysUserEntity.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            SysUserEntity userDb = selectByLoginName(req.getLoginName());
            if (!ObjectUtils.isEmpty(userDb)) {
                throw new RuntimeException("The user name already exists");
            }
            user.setId(snowFlake.nextId());
            sysUserMapper.insert(user);
        }
    }

    @Override
    public SysUserLoginResp login(SysUserLoginReq req) {
        SysUserEntity userDb = selectByLoginName(req.getLoginName());
        if (ObjectUtils.isEmpty(userDb)) {
            throw new RuntimeException("User not found");
        } else if (!userDb.getPassword().equals(req.getPassword())) {
            throw new RuntimeException("Incorrect password");
        } else {
            return CopyUtil.copy(userDb, SysUserLoginResp.class);
        }
    }

    public SysUserEntity selectByLoginName(String loginName) {
        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(SysUserEntity::getLoginName, loginName);
        List<SysUserEntity> userEntityList = sysUserMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(userEntityList)) {
            return null;
        } else {
            return userEntityList.get(0);
        }
    }

    @Override
    public void deleteUserById(Long id) {
        if (id == null) {
            throw new RuntimeException("Invalid ID: ID cannot be null");
        }
        System.out.println("Attempting to delete user with ID: " + id);

        int deletedRows = sysUserMapper.deleteById(id);
        System.out.println("Deleted rows: " + deletedRows);

        if (deletedRows == 0) {
            throw new RuntimeException("User not found, deletion failed");
        }
    }

    @Override
    public List<SysUserEntity> searchUsersByName(String name) {
        QueryWrapper<SysUserEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().like(SysUserEntity::getLoginName, name);
        return sysUserMapper.selectList(wrapper);
    }

    @Override
    public void deleteByLoginName(String username) {

    }


}
