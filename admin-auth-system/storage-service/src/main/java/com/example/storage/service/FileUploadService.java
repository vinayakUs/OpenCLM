package com.example.storage.service;

import com.example.storage.dto.FileUploadResponse;
import com.example.storage.dto.InternalFileResponse;
import com.example.storage.entity.FileStorage;
import com.example.storage.exception.FileNotFoundException;
import com.example.storage.exception.FileStorageException;
import com.example.storage.repository.FileStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Service s3Service;
    private final FileStorageRepository fileStorageRepository;

    @org.springframework.transaction.annotation.Transactional
    public FileUploadResponse uploadFile(MultipartFile file, UUID uploadedBy) {

        String key = buildKey(file);
        String s3Path = s3Service.uploadFile(key, file);

        try {
            FileStorage entity = new FileStorage();
            entity.setOriginalName(file.getOriginalFilename());
            entity.setFilePath(s3Path);
            entity.setMimeType(file.getContentType());
            entity.setSizeInBytes(file.getSize());
            entity.setUploadedBy(uploadedBy);

            fileStorageRepository.save(entity);

            return new FileUploadResponse(
                    entity.getId(),
                    entity.getOriginalName(),
                    entity.getFilePath(),
                    entity.getMimeType(),
                    entity.getSizeInBytes());
        } catch (Exception e) {
            // Compensation: Delete file from S3 if DB save fails
            s3Service.deleteFile(key);
            throw new FileStorageException("Failed to save file metadata, rolled back S3 upload", e);
        }
    }

    private String buildKey(MultipartFile file) {
        String extension = getFileExtension(file.getOriginalFilename());
        String id = UUID.randomUUID()
                .toString()
                .replace("-", "");

        // Shard prefix
        String p1 = id.substring(0, 2);
        String p2 = id.substring(2, 4);

        return "uploads/" + p1 + "/" + p2 + "/" + id + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "";
        }

        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot);
    }

    public CompletableFuture<InternalFileResponse> downloadFile(UUID id) {

        FileStorage fileStorage = fileStorageRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Record not found for ID: " + id, null));

        return s3Service.downloadFile(fileStorage.getFilePath())
                .thenApply(bytes -> {
                    InternalFileResponse response = new InternalFileResponse();
                    response.setFileName(fileStorage.getOriginalName());
                    response.setExt(fileStorage.getMimeType());
                    response.setFile(bytes);
                    return response;
                });
    }
}
