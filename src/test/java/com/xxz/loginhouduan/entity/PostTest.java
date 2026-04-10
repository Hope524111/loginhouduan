package com.xxz.loginhouduan.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PostTest {

    private Post post;

    @BeforeEach
    public void setup() {
        post = new Post();
        post.setId(1L);
        post.setUserName("alice");
        post.setContent("Hello world");
        post.setTimeAgo("2 minutes ago");
        post.setLikes(5);
        post.setImages(Arrays.asList("img1.png", "img2.jpg"));
        post.setVideo("video.mp4");
        post.setLikedUsers(new HashSet<>(Arrays.asList("bob", "carol")));

        Comment comment = new Comment();
        comment.setId(10L);
        comment.setContent("Nice post!");
        post.setComments(new HashSet<>(Collections.singleton(comment)));

        post.setIsLiked(true);
    }

    @Test
    public void testGetters() {
        assertEquals(1L, post.getId());
        assertEquals("alice", post.getUserName());
        assertEquals("Hello world", post.getContent());
        assertEquals("2 minutes ago", post.getTimeAgo());
        assertEquals(5, post.getLikes());
        assertEquals("video.mp4", post.getVideo());
        assertEquals(2, post.getImages().size());
        assertTrue(post.getLikedUsers().contains("bob"));
        assertTrue(post.getIsLiked());
        assertEquals(1, post.getComments().size());
    }

    @Test
    public void testSetters() {
        post.setId(2L);
        post.setUserName("bob");
        post.setLikes(10);
        post.setIsLiked(false);

        assertEquals(2L, post.getId());
        assertEquals("bob", post.getUserName());
        assertEquals(10, post.getLikes());
        assertFalse(post.getIsLiked());
    }

    @Test
    public void testEqualsAndHashCode() {
        Post another = new Post();
        another.setId(1L);

        assertEquals(post, another);
        assertEquals(post.hashCode(), another.hashCode());

        Post different = new Post();
        different.setId(2L);
        assertNotEquals(post, different);
    }

    @Test
    public void testToString() {
        String result = post.toString();
        assertNotNull(result);
        assertTrue(result.contains("alice"));
        assertTrue(result.contains("Hello world"));
    }

    @Test
    public void testOnCreate() {
        Post newPost = new Post();
        assertNull(newPost.getCreatedAt());
        newPost.onCreate();
        assertNotNull(newPost.getCreatedAt());
    }

    @Test
    public void testLikedUsersEmptySafe() {
        Post p = new Post();
        assertNotNull(p.getLikedUsers());
        assertTrue(p.getLikedUsers().isEmpty());
    }
}
