package com.example.bff.domain.document.service;

import com.example.bff.domain.document.client.StorageClient;
import com.example.bff.domain.document.dto.FileUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final StorageClient storageClient;

    public FileUploadResponse uploadDocument(MultipartFile file) {

        return storageClient.storeFileS3(file);

    }

}


