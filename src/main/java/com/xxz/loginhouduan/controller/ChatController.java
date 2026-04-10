package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.repository.UserRepository;
import com.xxz.loginhouduan.service.OpenAIService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:8080")
public class ChatController {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private UserRepository userRepository;

    private final Gson gson = new Gson();

    private String formatAiResponse(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "<p class='ai-response'>No response received.</p>";
        }

        StringBuilder formatted = new StringBuilder();
        String[] lines = content.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.matches("^[0-9]+\\..*")) {
                formatted.append("<h3 class='ai-heading'>").append(line).append("</h3>");
            } else {
                formatted.append("<p class='ai-response'>").append(line).append("</p>");
            }
        }

        return formatted.toString();
    }

    @PostMapping
    public Map<String, String> chat(@RequestBody(required = false) Map<String, String> request) {
        // 🛠 1. Ensure request parameters are not null
        if (request == null || !request.containsKey("loginName") || !request.containsKey("userMessage")) {
            throw new IllegalArgumentException("Invalid request: missing loginName or userMessage");
        }

        String loginName = request.get("loginName");
        String userMessage = request.get("userMessage");

        // 🛠 2. Ensure loginName and userMessage are not empty
        if (loginName == null || loginName.trim().isEmpty()) {
            throw new IllegalArgumentException("loginName cannot be empty");
        }
        if (userMessage == null || userMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("userMessage cannot be empty");
        }

        // 🛠 3. Find the user to avoid null
        SysUserEntity user = userRepository.findByLoginName(loginName);
        if (user == null) {
            throw new RuntimeException("User not found: " + loginName);
        }

        // 🛠 4. Get user chat history, ensure it's not null
        List<Map<String, String>> chatHistory = new ArrayList<>();
        if (user.getChatHistory() != null && !user.getChatHistory().trim().isEmpty()) {
            chatHistory = gson.fromJson(user.getChatHistory(), new TypeToken<List<Map<String, String>>>() {}.getType());
        }

        // Add user message
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        chatHistory.add(userMsg);

        // 🛠 5. Ensure AI response is not null
        String aiResponse = openAIService.getChatResponse(userMessage);
        if (aiResponse == null) {
            aiResponse = "Sorry, I couldn't process your request.";
        }

        // Add AI response
        Map<String, String> aiMsg = new HashMap<>();
        aiMsg.put("role", "assistant");
        aiMsg.put("content", formatAiResponse(aiResponse));
        chatHistory.add(aiMsg);

        // 🛠 6. Store new chat history
        user.setChatHistory(gson.toJson(chatHistory));
        userRepository.updateChatHistory(user.getId(), user.getChatHistory());

        return aiMsg;
    }

    @GetMapping("/{loginName}")
    public List<Map<String, String>> getChatHistory(@PathVariable String loginName) {
        if (loginName == null || loginName.trim().isEmpty()) {
            throw new IllegalArgumentException("loginName cannot be empty");
        }

        SysUserEntity user = userRepository.findByLoginName(loginName);
        if (user == null) {
            throw new RuntimeException("User not found: " + loginName);
        }

        if (user.getChatHistory() == null || user.getChatHistory().trim().isEmpty()) {
            return new ArrayList<>();
        }

        return gson.fromJson(user.getChatHistory(), new TypeToken<List<Map<String, String>>>() {}.getType());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return errorResponse;
    }
}
