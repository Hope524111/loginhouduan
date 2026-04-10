package com.xxz.loginhouduan.controller;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UploadController.class)
public class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final String UPLOAD_PATH = System.getProperty("user.dir") + "/uploads/";

    @BeforeEach
    public void cleanUploadFolder() throws Exception {
        File folder = new File(UPLOAD_PATH);
        if (folder.exists()) {
            for (File file : folder.listFiles()) {
                file.delete();
            }
        } else {
            folder.mkdirs();
        }
    }

    @Test
    public void testUploadImage_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-image.png", MediaType.IMAGE_PNG_VALUE, "fake image content".getBytes()
        );

        mockMvc.perform(multipart("/api/upload/image").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists());
    }

    @Test
    public void testUploadVideo_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-video.mp4", "video/mp4", "fake video content".getBytes()
        );

        mockMvc.perform(multipart("/api/upload/video").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").exists());
    }

    @Test
    public void testUploadImage_emptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.txt", MediaType.TEXT_PLAIN_VALUE, new byte[0]
        );

        mockMvc.perform(multipart("/api/upload/image").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("上传失败：文件为空"));
    }

    @Test
    public void testServeFile_success() throws Exception {
        String fileName = "test-file.txt";
        File testFile = new File(UPLOAD_PATH + fileName);
        FileUtils.writeStringToFile(testFile, "sample content", StandardCharsets.UTF_8);

        mockMvc.perform(get("/api/upload/uploads/" + fileName))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\""))
                .andExpect(content().string("sample content"));
    }

    @Test
    public void testServeFile_notFound() throws Exception {
        mockMvc.perform(get("/api/upload/uploads/nonexistent.txt"))
                .andExpect(status().isNotFound());
    }
}
