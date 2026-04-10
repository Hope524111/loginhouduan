package com.xxz.loginhouduan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxz.loginhouduan.entity.Message;
import com.xxz.loginhouduan.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private Message message;

    @BeforeEach
    public void setup() {
        message = new Message();
        message.setId(1L);
        message.setSender("alice");
        message.setReceiver("bob");
        message.setContent("Hello Bob!");
        message.setTimestamp(LocalDateTime.now());
    }

    @Test
    public void testSendMessage_success() throws Exception {
        Mockito.when(messageService.saveMessage(any(Message.class))).thenReturn(message);

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sender").value("alice"))
                .andExpect(jsonPath("$.receiver").value("bob"))
                .andExpect(jsonPath("$.content").value("Hello Bob!"));
    }

    @Test
    public void testSendMessage_invalid() throws Exception {
        Message badMessage = new Message(); // missing fields

        mockMvc.perform(post("/api/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badMessage)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("sender/receiver/content cannot be null!"));
    }

    @Test
    public void testGetMessages_success() throws Exception {
        List<Message> messages = Arrays.asList(message);

        Mockito.when(messageService.getMessages(eq("alice"), eq("bob")))
                .thenReturn(messages);

        mockMvc.perform(get("/api/messages")
                        .param("sender", "alice")
                        .param("receiverId", "bob"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sender").value("alice"))
                .andExpect(jsonPath("$[0].receiver").value("bob"))
                .andExpect(jsonPath("$[0].content").value("Hello Bob!"));
    }
}
