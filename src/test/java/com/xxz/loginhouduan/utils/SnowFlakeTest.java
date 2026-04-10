package com.xxz.loginhouduan.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SnowFlakeTest {

    // Test if generated IDs are unique and increasing
    @Test
    void testNextId_uniqueness() {
        SnowFlake snowFlake = new SnowFlake(1, 1);
        long id1 = snowFlake.nextId();
        long id2 = snowFlake.nextId();

        assertNotEquals(id1, id2); // Each ID should be unique
        assertTrue(id2 > id1); // IDs should be increasing
    }

    // Test custom constructor with valid parameters
    @Test
    void testConstructor_validValues() {
        assertDoesNotThrow(() -> new SnowFlake(0, 0)); // minimum valid
        assertDoesNotThrow(() -> new SnowFlake(31, 31)); // maximum valid
    }

    // Test custom constructor with invalid data center ID
    @Test
    void testConstructor_invalidDatacenterId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SnowFlake(32, 1));
        assertTrue(exception.getMessage().contains("datacenterId can't be greater"));
    }

    // Test custom constructor with invalid machine ID
    @Test
    void testConstructor_invalidMachineId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SnowFlake(1, 32));
        assertTrue(exception.getMessage().contains("machineId can't be greater"));
    }

    // Test generating multiple IDs in a loop
    @Test
    void testNextId_batchGeneration() {
        SnowFlake snowFlake = new SnowFlake(1, 1);
        long previousId = snowFlake.nextId();
        for (int i = 0; i < 100; i++) {
            long newId = snowFlake.nextId();
            assertTrue(newId > previousId);
            previousId = newId;
        }
    }
}
