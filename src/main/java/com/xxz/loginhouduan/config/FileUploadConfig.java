package com.xxz.loginhouduan.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import javax.servlet.MultipartConfigElement;

@Configuration
public class FileUploadConfig {
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.parse("50MB")); // 允许最大单个文件50MB
        factory.setMaxRequestSize(DataSize.parse("100MB")); // 允许最大请求大小100MB
        return factory.createMultipartConfig();
    }
}

