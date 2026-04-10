package com.xxz.loginhouduan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxz.loginhouduan.entity.Comment;
import com.xxz.loginhouduan.entity.Post;
import com.xxz.loginhouduan.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    private Post post;
    private Comment comment;

    @BeforeEach
    public void setup() {
        post = new Post();
        post.setId(1L);
        post.setContent("Test content");
        post.setUserName("alice");

        comment = new Comment();
        comment.setId(1L);
        comment.setPost(post);
        comment.setUserName("bob");
        comment.setContent("Nice post!");
    }

    @Test
    public void testCreatePost() throws Exception {
        Mockito.when(postService.createPost(any(Post.class))).thenReturn(post);

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("alice"))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    public void testDeletePost_success() throws Exception {
        Mockito.when(postService.deletePost(eq(1L), eq("alice"))).thenReturn(true);

        mockMvc.perform(delete("/api/posts/1")
                        .param("userName", "alice"))
                .andExpect(status().isOk())
                .andExpect(content().string("Post deleted successfully"));
    }

    @Test
    public void testLikePost() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("likes", 1);
        Mockito.when(postService.likePost(eq(1L), eq("alice"))).thenReturn(response);

        Map<String, String> request = new HashMap<>();
        request.put("userName", "alice");

        mockMvc.perform(post("/api/posts/1/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.likes").value(1));
    }

    @Test
    public void testAddComment() throws Exception {
        Mockito.when(postService.addComment(eq(1L), any(Comment.class))).thenReturn(comment);

        mockMvc.perform(post("/api/posts/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("bob"))
                .andExpect(jsonPath("$.content").value("Nice post!"));
    }

    @Test
    public void testGetAllPosts() throws Exception {
        Mockito.when(postService.getAllPosts(eq("alice"))).thenReturn(Collections.singletonList(post));

        mockMvc.perform(get("/api/posts")
                        .param("userName", "alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value("alice"))
                .andExpect(jsonPath("$[0].content").value("Test content"));
    }
}
