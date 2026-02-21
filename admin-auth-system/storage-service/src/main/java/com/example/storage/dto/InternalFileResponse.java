package com.example.storage.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalFileResponse {
    private byte[] file;
    private UUID fileId;
    private String fileName;
    private String ext;
    private long fileSize;
}
