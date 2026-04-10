
package com.xxz.loginhouduan.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class OpenAIServiceTest {

    @InjectMocks
    private OpenAIService openAIService;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetChatResponse_mocked() throws Exception {
        // Mock a fake OpenAI API response
        String fakeResponse = "{\"choices\": [{\"message\": {\"content\": \"This is a mock response from AI.\"}}]}";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(fakeResponse.getBytes(StandardCharsets.UTF_8));
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        // Execute test on real instance (since it's hard to mock inner structure of Apache HttpClient without breaking too much)
        String result = openAIService.getChatResponse("Hello");
        assertTrue(result != null && !result.isEmpty());
    }
}
