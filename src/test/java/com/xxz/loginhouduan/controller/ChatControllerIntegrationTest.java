package com.xxz.loginhouduan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.repository.UserRepository;
import com.xxz.loginhouduan.service.OpenAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
public class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAIService openAIService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SysUserEntity user;

    @BeforeEach
    public void setup() {
        user = new SysUserEntity();
        user.setId(1L);
        user.setLoginName("testuser");
        user.setChatHistory(null);

        Mockito.when(userRepository.findByLoginName("testuser")).thenReturn(user);
        Mockito.when(openAIService.getChatResponse(anyString())).thenReturn("This is a test response.");
    }

    @Test
    public void testChatSuccess() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"testuser\",\"userMessage\":\"Hello AI\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("assistant"))
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testChatWithMissingParams() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"testuser\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testChat_loginNameIsEmpty_shouldFail() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"\",\"userMessage\":\"Hello\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("loginName cannot be empty"));
    }

    @Test
    public void testChat_userMessageIsEmpty_shouldFail() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"testuser\",\"userMessage\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("userMessage cannot be empty"));
    }

    @Test
    public void testChat_userNotFound_shouldFail() throws Exception {
        Mockito.when(userRepository.findByLoginName("ghost")).thenReturn(null);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"ghost\",\"userMessage\":\"Hello\"}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testChat_requestBodyMissing_shouldFail() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request: missing loginName or userMessage"));
    }

    @Test
    public void testChat_aiResponseNull_shouldFallbackMessage() throws Exception {
        Mockito.when(openAIService.getChatResponse(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"testuser\",\"userMessage\":\"Hi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("<p class='ai-response'>Sorry, I couldn't process your request.</p>"));
    }

    @Test
    public void testGetChatHistoryEmpty() throws Exception {
        mockMvc.perform(get("/api/chat/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void testGetChatHistoryWithContent() throws Exception {
        user.setChatHistory("[{\"role\":\"user\",\"content\":\"Hi\"}]");

        mockMvc.perform(get("/api/chat/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].role").value("user"))
                .andExpect(jsonPath("$[0].content").value("Hi"));
    }

    @Test
    public void testGetChatHistory_appendToExisting() throws Exception {
        user.setChatHistory("[{\"role\":\"user\",\"content\":\"Hi\"},{\"role\":\"assistant\",\"content\":\"Hello\"}]");

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginName\":\"testuser\",\"userMessage\":\"What's up?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("assistant"))
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testGetChatHistory_loginNameEmpty_shouldFail() throws Exception {
        mockMvc.perform(get("/api/chat/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("loginName cannot be empty"));
    }

    @Test
    public void testGetChatHistory_userNotFound_shouldFail() throws Exception {
        Mockito.when(userRepository.findByLoginName("ghost")).thenReturn(null);

        mockMvc.perform(get("/api/chat/ghost"))
                .andExpect(status().isInternalServerError());
    }
}
