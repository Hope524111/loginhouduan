package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.FriendRelation;
import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.FriendRelationMapper;
import com.xxz.loginhouduan.mapper.SysUserMapper;
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

@WebMvcTest(FriendController.class)
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SysUserMapper sysUserMapper;

    @MockBean
    private FriendRelationMapper friendRelationMapper;

    private SysUserEntity user1;
    private SysUserEntity user2;

    @BeforeEach
    public void setup() {
        user1 = new SysUserEntity();
        user1.setId(1L);
        user1.setLoginName("user1");

        user2 = new SysUserEntity();
        user2.setId(2L);
        user2.setLoginName("user2");

        Mockito.when(sysUserMapper.selectOne(any())).thenReturn(user1);
        Mockito.when(sysUserMapper.selectById(eq(2L))).thenReturn(user2);
    }

    @Test
    public void testAddFriend_success() throws Exception {
        Mockito.when(friendRelationMapper.selectOne(any())).thenReturn(null);
        Mockito.when(friendRelationMapper.insert(any())).thenReturn(1);

        String payload = "{\"userName\":\"user1\",\"friendId\":2}";
        mockMvc.perform(post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend added successfully"));
    }

    @Test
    public void testAddFriend_alreadyFriends() throws Exception {
        Mockito.when(friendRelationMapper.selectOne(any())).thenReturn(new FriendRelation());

        String payload = "{\"userName\":\"user1\",\"friendId\":2}";
        mockMvc.perform(post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Already friends"));
    }

    @Test
    public void testAddFriend_addSelf() throws Exception {
        user2.setId(1L); // mock self-add
        Mockito.when(sysUserMapper.selectById(eq(1L))).thenReturn(user2);

        String payload = "{\"userName\":\"user1\",\"friendId\":1}";
        mockMvc.perform(post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Cannot add yourself as a friend"));
    }

    @Test
    public void testGetFriendList() throws Exception {
        Map<String, Object> friend = new HashMap<>();
        friend.put("id", 2);
        friend.put("login_name", "user2");

        List<Map<String, Object>> friends = new ArrayList<>();
        friends.add(friend);

        Mockito.when(friendRelationMapper.getFriends(eq(1L))).thenReturn(friends);

        mockMvc.perform(get("/api/friends/list?userName=user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login_name").value("user2"));
    }

    @Test
    public void testAddFriend_missingParameters_shouldFail() throws Exception {
        String payload = "{\"userName\":\"user1\"}"; // 缺少 friendId
        mockMvc.perform(post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Missing parameters"));
    }

    @Test
    public void testAddFriend_invalidFriendId_shouldFail() throws Exception {
        String payload = "{\"userName\":\"user1\",\"friendId\":\"abc\"}"; // friendId 非数字
        mockMvc.perform(post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Invalid friendId parameter"));
    }

    @Test
    public void testAddFriend_userDoesNotExist_shouldFail() throws Exception {
        Mockito.when(sysUserMapper.selectOne(any())).thenReturn(null); // 当前用户不存在

        String payload = "{\"userName\":\"user1\",\"friendId\":2}";
        mockMvc.perform(post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("User does not exist"));
    }

    @Test
    public void testAddFriend_friendUserDoesNotExist_shouldFail() throws Exception {
        Mockito.when(sysUserMapper.selectById(eq(2L))).thenReturn(null); // 好友不存在

        String payload = "{\"userName\":\"user1\",\"friendId\":2}";
        mockMvc.perform(post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("User does not exist"));
    }

    @Test
    public void testAddFriend_insertFailed_shouldFail() throws Exception {
        Mockito.when(friendRelationMapper.selectOne(any())).thenReturn(null);
        Mockito.when(friendRelationMapper.insert(any())).thenReturn(0); // 插入失败

        String payload = "{\"userName\":\"user1\",\"friendId\":2}";
        mockMvc.perform(post("/api/friends/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Failed to add friend"));
    }

}
