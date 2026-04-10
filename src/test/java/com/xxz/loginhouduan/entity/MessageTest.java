package com.xxz.loginhouduan.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @Test
    public void testEquals_sameObject() {
        Message msg = new Message();
        msg.setId(1L);
        assertEquals(msg, msg);
    }

    @Test
    public void testEquals_nullObject() {
        Message msg = new Message();
        msg.setId(1L);
        assertNotEquals(msg, null);
    }

    @Test
    public void testEquals_differentClass() {
        Message msg = new Message();
        msg.setId(1L);
        assertNotEquals(msg, "String");
    }

    @Test
    public void testEquals_sameId() {
        Message msg1 = new Message();
        msg1.setId(1L);
        Message msg2 = new Message();
        msg2.setId(1L);
        assertEquals(msg1, msg2);
    }

    @Test
    public void testEquals_differentId() {
        Message msg1 = new Message();
        msg1.setId(1L);
        Message msg2 = new Message();
        msg2.setId(2L);
        assertNotEquals(msg1, msg2);
    }

    @Test
    public void testHashCode_consistentWithEquals() {
        Message msg1 = new Message();
        msg1.setId(1L);
        Message msg2 = new Message();
        msg2.setId(1L);
        assertEquals(msg1.hashCode(), msg2.hashCode());
    }

    @Test
    public void testToString_notNull() {
        Message msg = new Message();
        msg.setId(1L);
        msg.setSender("A");
        msg.setReceiver("B");
        msg.setContent("Hello");
        msg.setTimestamp(LocalDateTime.now());
        assertNotNull(msg.toString());
    }
}
