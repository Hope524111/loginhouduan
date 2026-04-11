package com.xxz.loginhouduan.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAIService {
    private static final String MINIMAX_API_URL = "https://api.minimaxi.com/v1/text/chatcompletion_v2";
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");

    // Used to store the conversation context
    private final List<JsonObject> messages = new ArrayList<>();

    public OpenAIService() {
        // Initialize system message to set up the assistant's behavior and ask for user details
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("name", "MiniMax AI");
        systemMessage.addProperty("content",
                "You are a professional fitness and nutrition assistant. "
                        + "Your role is to help users create personalized workout routines and diet plans based on their individual needs. "
                        + "Before providing any recommendations, always ask the user for the following details:\n"
                        + "1. Age\n"
                        + "2. Gender\n"
                        + "3. Height and weight\n"
                        + "4. Fitness goal (e.g., weight loss, muscle gain, endurance improvement, general health)\n"
                        + "5. Current fitness level (beginner, intermediate, advanced)\n"
                        + "6. Any dietary restrictions or preferences\n"
                        + "7. Any existing medical conditions or injuries that may affect training\n"
                        + "Once you have gathered this information, provide tailored fitness and nutrition advice, ensuring safety and effectiveness. "
                        + "Use scientifically backed principles and always recommend consulting a certified trainer or nutritionist for medical concerns."
        );

        messages.add(systemMessage);
    }


    public String getChatResponse(String userMessage) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(MINIMAX_API_URL);

            // Set request headers
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + API_KEY);

            // Add user message to context
            JsonObject userMessageObject = new JsonObject();
            userMessageObject.addProperty("role", "user");
            userMessageObject.addProperty("name", "用户");
            userMessageObject.addProperty("content", userMessage);
            messages.add(userMessageObject);

            // Prepare request body for MiniMax API
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", "abab5.5-chat");
            requestBody.addProperty("max_completion_tokens", 2048);
            requestBody.addProperty("temperature", 1.0);
            requestBody.addProperty("top_p", 0.95);

            JsonArray messagesArray = new JsonArray();
            for (JsonObject message : messages) {
                messagesArray.add(message);
            }
            requestBody.add("messages", messagesArray);

            StringEntity entity = new StringEntity(requestBody.toString(), "UTF-8");
            request.setEntity(entity);


            // Send the request
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // Debug: print raw response
                System.out.println("MiniMax Raw Response: " + result.toString());

                // Parse the MiniMax response
                JsonObject jsonResponse = new com.google.gson.JsonParser().parse(result.toString()).getAsJsonObject();

                // Debug: print full response
                System.out.println("MiniMax Full Response: " + jsonResponse.toString());

                // Check for error in response
                if (jsonResponse.has("error")) {
                    String errorMsg = jsonResponse.getAsJsonObject("error").get("message").getAsString();
                    return "API Error: " + errorMsg;
                }

                // Check for base_resp error (MiniMax specific)
                if (jsonResponse.has("base_resp")) {
                    JsonObject baseResp = jsonResponse.getAsJsonObject("base_resp");
                    int statusCode = baseResp.get("status_code").getAsInt();
                    String statusMsg = baseResp.get("status_msg").getAsString();
                    if (statusCode != 0) {
                        return "API Error: " + statusMsg;
                    }
                }

                // MiniMax uses "choices" array
                if (!jsonResponse.has("choices")) {
                    return "No response from AI. Please try again.";
                }

                JsonElement choicesElement = jsonResponse.get("choices");
                if (choicesElement == null || choicesElement.isJsonNull() || !choicesElement.isJsonArray()) {
                    return "No response from AI. Please try again.";
                }

                JsonArray choices = choicesElement.getAsJsonArray();
                if (choices.isEmpty()) {
                    return "No response from AI. Please try again.";
                }

                String assistantMessage = choices
                        .get(0)
                        .getAsJsonObject()
                        .get("message")
                        .getAsJsonObject()
                        .get("content")
                        .getAsString();

                // Add assistant's response to the context
                JsonObject assistantMessageObject = new JsonObject();
                assistantMessageObject.addProperty("role", "assistant");
                assistantMessageObject.addProperty("name", "MiniMax AI");
                assistantMessageObject.addProperty("content", assistantMessage);
                messages.add(assistantMessageObject);

                return assistantMessage;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}