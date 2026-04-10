package com.xxz.loginhouduan.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    private Comment comment;

    @BeforeEach
    public void setup() {
        comment = new Comment();
        comment.setId(1L);
        comment.setUserName("alice");
        comment.setContent("Nice post!");
        comment.setTimeAgo("1 hour ago");
        comment.setCreatedAt(new Date());
    }

    @Test
    public void testGetters() {
        assertEquals(1L, comment.getId());
        assertEquals("alice", comment.getUserName());
        assertEquals("Nice post!", comment.getContent());
        assertEquals("1 hour ago", comment.getTimeAgo());
        assertNotNull(comment.getCreatedAt());
    }

    @Test
    public void testEquals_sameObject_shouldBeTrue() {
        assertEquals(comment, comment);
    }

    @Test
    public void testEquals_null_shouldBeFalse() {
        assertNotEquals(comment, null);
    }

    @Test
    public void testEquals_differentClass_shouldBeFalse() {
        assertNotEquals(comment, new Object());
    }

    @Test
    public void testEquals_differentId_shouldBeFalse() {
        Comment other = new Comment();
        other.setId(2L);
        assertNotEquals(comment, other);
    }

    @Test
    public void testEquals_sameId_shouldBeTrue() {
        Comment other = new Comment();
        other.setId(1L);
        assertEquals(comment, other);
    }

    @Test
    public void testHashCode_consistency() {
        Comment another = new Comment();
        another.setId(1L);
        assertEquals(comment.hashCode(), another.hashCode());
    }

    @Test
    public void testToString_notNull() {
        assertNotNull(comment.toString());
        assertTrue(comment.toString().contains("alice"));
    }
}
