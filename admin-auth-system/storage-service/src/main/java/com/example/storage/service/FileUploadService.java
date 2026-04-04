package com.example.storage.service;

import com.example.storage.dto.FileUploadResponse;
import com.example.storage.dto.InternalFileResponse;
import com.example.storage.entity.FileStorage;
import com.example.storage.exception.FileDeleteException;
import com.example.storage.exception.FileDownloadException;
import com.example.storage.exception.FileNotFoundException;
import com.example.storage.exception.FileUploadException;
import com.example.storage.repository.FileStorageRepository;
import com.example.storage.service.provider.StorageProvider;
import com.example.storage.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final FileStorageRepository fileStorageRepository;
    private final UuidShardedKeyGenerator uuidShardedKeyGenerator;
    private final StorageProvider storageProvider;
    private final TransactionTemplate transactionTemplate;

    public FileUploadResponse uploadFile(MultipartFile file, UUID uploadedBy) {

        String key = uuidShardedKeyGenerator.generateKey(file);

        String storagePath = storageProvider.uploadFile(key, file);

        try {
            return transactionTemplate.execute(
                    status -> {
                        FileStorage entity = new FileStorage();
                        entity.setOriginalName(file.getOriginalFilename());
                        entity.setFilePath(storagePath);
                        entity.setMimeType(file.getContentType());
                        entity.setSizeInBytes(file.getSize());
                        entity.setUploadedBy(uploadedBy);
                        entity.setStorageKey(key);
                        fileStorageRepository.save(entity);
                        return new FileUploadResponse(
                                entity.getId(),
                                entity.getOriginalName(),
                                entity.getMimeType(),
                                entity.getSizeInBytes());
                    });
        } catch (Exception e) {
            try {
                storageProvider.deleteFile(key);
            } catch (Exception ex) {
                log.error("error for delete file {}", ex.getMessage(), ex);
            }
            throw new FileUploadException("Failed to persist file metadata", e);
        }

    }

    public InternalFileResponse downloadFile(UUID id) {

        FileStorage fileStorage = fileStorageRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Record not found for ID: " + id, null));
        try {
            byte[] fileBytes = storageProvider.downloadFile(fileStorage.getStorageKey());
            InternalFileResponse response = new InternalFileResponse();
            response.setFileSize(fileBytes.length);
            response.setFileName(fileStorage.getOriginalName());
            response.setExt(FileUtils.getFileExtension(fileStorage.getOriginalName()));
            response.setFile(fileBytes);
            response.setFileId(id);
            return response;
        } catch (Exception e) {
            throw new FileDownloadException("Failed to download file", e);
        }

    }

    public void deleteFile(UUID id) {

        FileStorage fileStorage = fileStorageRepository.findById(id).orElseThrow(
                () -> new FileNotFoundException("Record not found for ID: " + id, null));

        transactionTemplate.execute(status -> {
            fileStorageRepository.deleteById(fileStorage.getId());
            return null;
        });

        try {
            storageProvider.deleteFile(fileStorage.getStorageKey());
        } catch (Exception e) {
            log.error("Storage delete failed. Restoring DB record for file: {}", fileStorage.getId());
            // Because we hold the detached entity in memory, we can save() it back to the
            // DB
            // to restore the pointer!
            fileStorageRepository.save(fileStorage);

            throw new FileDeleteException("Failed to delete file from storage provider", e);
        }

    }
}
