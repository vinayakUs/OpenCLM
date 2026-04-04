package com.example.storage.controller;

import com.example.common.dto.Api.ApiResponseV2;
import com.example.storage.dto.FileUploadResponse;
import com.example.storage.dto.InternalFileResponse;
import com.example.storage.service.FileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/storage/files")
@RequiredArgsConstructor
@Validated
public class FileStorageController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponseV2<FileUploadResponse>> upload(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {

        FileUploadResponse response = fileUploadService.uploadFile(file, UUID.fromString(jwt.getSubject()));
        ApiResponseV2<FileUploadResponse> r = ApiResponseV2.success(response);
        r.setTraceId(MDC.get("traceId"));
        r.setPath(request.getRequestURI());

        return ResponseEntity.ok(r);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam(name = "id") UUID id) {

        InternalFileResponse res = fileUploadService.downloadFile(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + res.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(res.getFileSize()))
                .body(res.getFile());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponseV2<Void>> deleteFile(
            @RequestParam(name = "id") UUID id, HttpServletRequest request) {

        fileUploadService.deleteFile(id);
        ApiResponseV2<Void> response = ApiResponseV2.success(null);
        response.setPath(request.getRequestURI());
        response.setTraceId(MDC.get("traceId"));
        return ResponseEntity.ok(response);

    }

}
