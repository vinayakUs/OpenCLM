package com.example.storageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FileUploadResponse {
    private UUID fileId;
    private String originalName;
    private String filePath;
    private String mimeType;
    private Long size;
}
