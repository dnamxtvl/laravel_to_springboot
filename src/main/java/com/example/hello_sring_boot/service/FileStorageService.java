package com.example.hello_sring_boot.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service

public class FileStorageService {
    private final Path root = Paths.get("uploads");

    public String save(MultipartFile file) {
        try {
            if (!Files.exists(root)) Files.createDirectories(root);
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(fileName));

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Không thể lưu file: " + e.getMessage());
        }
    }
}
