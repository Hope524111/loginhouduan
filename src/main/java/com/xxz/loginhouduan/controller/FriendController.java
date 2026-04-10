package com.xxz.loginhouduan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxz.loginhouduan.entity.FriendRelation;
import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.FriendRelationMapper;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private FriendRelationMapper friendRelationMapper;

    @PostMapping("/add")
    public String addFriend(@RequestBody Map<String, Object> payload) {
        System.out.println("🔥 Received payload from frontend: " + payload);

        // 1. Safely parse parameters
        String userName = (String) payload.get("userName");
        String friendIdStr = String.valueOf(payload.get("friendId"));

        if (userName == null || friendIdStr == null || friendIdStr.equals("null")) {
            System.out.println("❌ Missing parameters! userName=" + userName + " | friendId=" + friendIdStr);
            return "Missing parameters";
        }

        Long friendId;
        try {
            friendId = new BigDecimal(friendIdStr).longValue();
        } catch (Exception e) {
            System.out.println("❌ Error parsing friendId: " + e.getMessage());
            return "Invalid friendId parameter";
        }

        System.out.println("✅ Current user: " + userName);
        System.out.println("✅ Target friend ID: " + friendId);

        // 2. Check if users exist
        SysUserEntity currentUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUserEntity>().eq("login_name", userName));
        SysUserEntity friendUser = sysUserMapper.selectById(friendId);

        if (currentUser == null || friendUser == null) {
            System.out.println("❌ User does not exist: currentUser=" + currentUser + ", friendUser=" + friendUser);
            return "User does not exist";
        }
        if (currentUser.getId().equals(friendId)) {
            System.out.println("⚠️ Cannot add yourself as a friend");
            return "Cannot add yourself as a friend";
        }

        // 3. Check if already friends
        QueryWrapper<FriendRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUser.getId()).eq("friend_id", friendId);
        if (friendRelationMapper.selectOne(wrapper) != null) {
            System.out.println("⚠️ Already friends: " + currentUser.getLoginName() + " → " + friendId);
            return "Already friends";
        }

        // 4. Insert friend relation
        FriendRelation relation = new FriendRelation();
        relation.setUserId(currentUser.getId());
        relation.setFriendId(friendId);
        int result = friendRelationMapper.insert(relation);

        if (result > 0) {
            System.out.println("✅ Insert successful! Rows affected: " + result);
            return "Friend added successfully";
        } else {
            System.out.println("❌ Insert failed!");
            return "Failed to add friend";
        }
    }

    @GetMapping("/list")
    public List<Map<String, Object>> getFriends(@RequestParam String userName) {
        SysUserEntity user = sysUserMapper.selectOne(
                new QueryWrapper<SysUserEntity>().eq("login_name", userName));
        return friendRelationMapper.getFriends(user.getId());
    }
}
