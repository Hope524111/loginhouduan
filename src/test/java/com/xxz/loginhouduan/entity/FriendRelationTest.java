package com.xxz.loginhouduan.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class FriendRelationTest {

    @Test
    public void testGetterAndSetter() {
        Date now = new Date();
        FriendRelation relation = new FriendRelation();
        relation.setId(1L);
        relation.setUserId(10L);
        relation.setFriendId(20L);
        relation.setCreatedTime(now);

        assertEquals(1L, relation.getId());
        assertEquals(10L, relation.getUserId());
        assertEquals(20L, relation.getFriendId());
        assertEquals(now, relation.getCreatedTime());
    }

    @Test
    public void testEqualsAndHashCode_sameValues() {
        Date now = new Date();

        FriendRelation r1 = new FriendRelation();
        r1.setId(1L);
        r1.setUserId(10L);
        r1.setFriendId(20L);
        r1.setCreatedTime(now);

        FriendRelation r2 = new FriendRelation();
        r2.setId(1L);
        r2.setUserId(10L);
        r2.setFriendId(20L);
        r2.setCreatedTime(now);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    public void testEquals_differentValues() {
        FriendRelation r1 = new FriendRelation();
        r1.setId(1L);
        r1.setUserId(10L);
        r1.setFriendId(20L);

        FriendRelation r2 = new FriendRelation();
        r2.setId(2L); // different id
        r2.setUserId(10L);
        r2.setFriendId(20L);

        assertNotEquals(r1, r2);
    }

    @Test
    public void testEquals_withNullAndOtherObject() {
        FriendRelation r1 = new FriendRelation();
        r1.setId(1L);

        assertNotEquals(null, r1);
        assertNotEquals("Not a FriendRelation", r1);
    }

    @Test
    public void testToString_notNull() {
        FriendRelation r1 = new FriendRelation();
        r1.setId(1L);
        r1.setUserId(10L);
        r1.setFriendId(20L);
        r1.setCreatedTime(new Date());

        String str = r1.toString();
        assertNotNull(str);
        assertTrue(str.contains("userId"));
        assertTrue(str.contains("friendId"));
    }
}
