package com.xxz.loginhouduan.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class UploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        return handleFileUpload(file);
    }

    @PostMapping("/video")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        return handleFileUpload(file);
    }

    private ResponseEntity<?> handleFileUpload(MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("上传失败：文件为空");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID() + extension;
            File destinationFile = new File(UPLOAD_DIR + newFilename);

            if (!destinationFile.getParentFile().exists()) {
                destinationFile.getParentFile().mkdirs();
            }

            file.transferTo(destinationFile);

            String fileUrl = "/uploads/" + newFilename;
            return ResponseEntity.ok().body("{\"url\": \"" + fileUrl + "\"}");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("上传失败:" + e.getMessage());
        }
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @ControllerAdvice
    public static class FileUploadExceptionAdvice {

        @ExceptionHandler(  MaxUploadSizeExceededException.class)
        public ResponseEntity<?> handleMaxSizeException(MaxUploadSizeExceededException ex) {
            return ResponseEntity.status(413).body("❌ 上传失败：文件大小超过限制！最大允许 100MB");
        }
    }
}
