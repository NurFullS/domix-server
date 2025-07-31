package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            Map<String, Object> uploadResult = cloudinaryService.uploadFile(file.getBytes(),
                    file.getOriginalFilename());
            String imageUrl = (String) uploadResult.get("secure_url");
            return ResponseEntity.ok(Map.of("image_url", imageUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка загрузки файла");
        }
    }

}
