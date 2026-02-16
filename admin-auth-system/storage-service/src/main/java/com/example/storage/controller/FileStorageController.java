package com.example.storage.controller;

import com.example.common.dto.Api.ApiResponseV2;
import com.example.storage.dto.FileUploadResponse;
import com.example.storage.dto.InternalFileResponse;
import com.example.storage.service.FileUploadService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
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
    public ApiResponseV2<FileUploadResponse> upload(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {

        FileUploadResponse response = fileUploadService.uploadFile(file, UUID.fromString(jwt.getSubject()));
        return ApiResponseV2.success(response);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam(name = "id" ) @NotNull(message = "File id required") UUID id){

        InternalFileResponse res = fileUploadService.downloadFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION ,"attachment; filename=\"" + res.getFileName() + "\"")
                .body(res.getFile());
    }


}
