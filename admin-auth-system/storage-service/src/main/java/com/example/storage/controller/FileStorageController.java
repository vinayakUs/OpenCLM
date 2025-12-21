package com.example.storage.controller;

import com.example.storage.dto.FileUploadResponse;
import com.example.storage.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@RestController
@RequestMapping("/storage/files")
@RequiredArgsConstructor
public class FileStorageController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> upload(@RequestPart("file") MultipartFile file , @AuthenticationPrincipal Jwt jwt) {
        FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(file, UUID.fromString(jwt.getSubject()));

        return ResponseEntity.ok(fileUploadResponse);
    }

}
