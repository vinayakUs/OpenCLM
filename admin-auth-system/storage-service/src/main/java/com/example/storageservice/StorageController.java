package com.example.storageservice;

import io.awspring.cloud.s3.S3Template;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/storage")
public class StorageController {

    private final S3Template s3Template;

    public StorageController(S3Template s3Template) {
        this.s3Template = s3Template;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
            @RequestParam("bucket") String bucket) throws IOException {
        s3Template.upload(bucket, file.getOriginalFilename(), file.getInputStream());
        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }

    @GetMapping("/download")
    public ResponseEntity<String> generatePresignedUrl(@RequestParam("bucket") String bucket,
            @RequestParam("key") String key) {
        // Simple implementation - in real world would return presigned URL or stream
        // content
        // For now let's just confirm it's there
        boolean exists = s3Template.objectExists(bucket, key);
        return exists ? ResponseEntity.ok("File exists") : ResponseEntity.notFound().build();
    }
}
