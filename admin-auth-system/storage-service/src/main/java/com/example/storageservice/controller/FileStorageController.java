package com.example.storageservice.controller;

import com.example.storageservice.dto.FileUploadResponse;
import com.example.storageservice.services.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileStorageController {

    private final FileUploadService fileUploadService;


    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> upload(@RequestPart("file") MultipartFile file , @RequestParam("uploadedBy") UUID uploadedBy) {
        FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(file, uploadedBy);

        return ResponseEntity.ok(fileUploadResponse);
    }

}
