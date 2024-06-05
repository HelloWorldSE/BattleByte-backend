package com.battlebyte.battlebyte.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    private Message message = new Message();
    @Test
    void testEquals() {
        assert(message.equals(message));
    }

    @Test
    void canEqual() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void testToString() {
    }
}