package com.example.storage.controller;

import com.example.common.dto.Api.ApiResponseV2;
import com.example.storage.dto.FileUploadResponse;
import com.example.storage.dto.InternalFileResponse;
import com.example.storage.service.FileUploadService;
import lombok.RequiredArgsConstructor;
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
            @AuthenticationPrincipal Jwt jwt) {

//        ApiErrorV2 err = ApiErrorV2.<Void>builder().code("INTERNAL ERROR").message("ERROR FROM SERVER CLIENT").build();
//        ApiResponseV2<FileUploadResponse> r = new ApiResponseV2<>();
//        r.setError(err);

//        return  ResponseEntity.status(HttpStatusCode.valueOf(400)).body(null);

        FileUploadResponse response = fileUploadService.uploadFile(file, UUID.fromString(jwt.getSubject()));
        return ResponseEntity.ok(ApiResponseV2.success(response));
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
    public ApiResponseV2<Void> deleteFile(
            @RequestParam(name = "id") UUID id){

        fileUploadService.deleteFile(id);
        return ApiResponseV2.success(null);
    }

}
