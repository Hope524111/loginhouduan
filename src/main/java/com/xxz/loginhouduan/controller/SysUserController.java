package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.req.SysUserLoginReq;
import com.xxz.loginhouduan.req.SysUserSaveReq;
import com.xxz.loginhouduan.resp.CommonResp;
import com.xxz.loginhouduan.resp.SysUserLoginResp;
import com.xxz.loginhouduan.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys-user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/register")
    public CommonResp register(@RequestBody SysUserSaveReq req) {
        if (req.getPassword().length() < 2 || req.getPassword().length() > 9) {
            throw new RuntimeException("Password length should be between 2 and 9 characters");
        }
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp();
        try {
            sysUserService.register(req);
            resp.setSuccess(true);
            resp.setMessage("Registered successfully");
        } catch (RuntimeException e) {
            resp.setSuccess(false);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    @PostMapping("/login")
    public CommonResp login(@RequestBody SysUserLoginReq req) {
        req.setPassword(DigestUtils.md5DigestAsHex(req.getPassword().getBytes()));
        CommonResp resp = new CommonResp<>();
        try {
            SysUserLoginResp loginResp = sysUserService.login(req);
            resp.setContent(loginResp);
        } catch (RuntimeException e) {
            resp.setSuccess(false);
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    @Autowired
    SysUserMapper sysUserMapper;

    @GetMapping("/Admin")
    public List<SysUserEntity> getAllusers() {
        return sysUserMapper.findAll();
    }

    // 添加删除用户的接口
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            System.out.println("Received delete request for user ID: " + id);
            sysUserService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            System.err.println("Error during deletion: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public List<Map<String, Object>> searchUsers(@RequestParam String name) {
        List<SysUserEntity> list = sysUserService.searchUsersByName(name);
        return list.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", String.valueOf(user.getId()));
            map.put("loginName", user.getLoginName());
            map.put("email", user.getEmail());
            return map;
        }).collect(Collectors.toList());
    }
    @GetMapping("/get-id")
    public ResponseEntity<Long> getUserIdByLoginName(@RequestParam String loginName) {
        List<SysUserEntity> users = sysUserService.searchUsersByName(loginName);
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users.get(0).getId());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


}

