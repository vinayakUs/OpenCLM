package com.example.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponse {
    private UUID fileId;
    private String originalName;
    private String filePath;
    private String mimeType;
    private Long size;
}
