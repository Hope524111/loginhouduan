package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.controller.UploadController.FileUploadExceptionAdvice;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUploadExceptionAdviceTest {

    @Test
    public void testHandleMaxSizeException() {
        FileUploadExceptionAdvice advice = new FileUploadExceptionAdvice();
        ResponseEntity<?> response = advice.handleMaxSizeException(
                new MaxUploadSizeExceededException(100 * 1024 * 1024)
        );

        assertThat(response.getStatusCodeValue()).isEqualTo(413);
        assertThat(response.getBody()).isEqualTo("❌ 上传失败：文件大小超过限制！最大允许 100MB");
    }
}
