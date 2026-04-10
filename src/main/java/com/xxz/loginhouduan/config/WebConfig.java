package com.xxz.loginhouduan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ 1️⃣ Get the absolute path of the current project to ensure correctness
        String uploadDir = "./uploads/";  // `./uploads/` represents a relative path
        File uploadFolder = new File(uploadDir);

        // ✅ 2️⃣ Create the `uploads/` directory if it does not exist
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // ✅ 3️⃣ Map `/uploads/**` to the local `uploads/` directory
        String absoluteUploadPath = uploadFolder.getAbsolutePath();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + absoluteUploadPath + "/");
    }
}
