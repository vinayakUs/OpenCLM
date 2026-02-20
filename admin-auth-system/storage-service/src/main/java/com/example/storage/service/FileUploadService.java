package com.example.storage.service;

import com.example.storage.dto.FileUploadResponse;
import com.example.storage.dto.InternalFileResponse;
import com.example.storage.entity.FileStorage;
import com.example.storage.exception.FileNotFoundException;
import com.example.storage.exception.FileStorageException;
import com.example.storage.exception.FileUploadException;
import com.example.storage.repository.FileStorageRepository;
import com.example.storage.service.provider.StorageProvider;
import com.example.storage.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.logging.LoggingRebinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final S3Service s3Service;
    private final FileStorageRepository fileStorageRepository;
    private final UuidShardedKeyGenerator uuidShardedKeyGenerator;
    private final StorageProvider storageProvider;
    private final TransactionTemplate transactionTemplate;
    private final LoggingRebinder loggingRebinder;

    public FileUploadResponse uploadFile(MultipartFile file, UUID uploadedBy) {

        String key = uuidShardedKeyGenerator.generateKey(file);

        String storagePath = storageProvider.uploadFile(key,file);

        try{
            return  transactionTemplate.execute(
                    status -> {
                        FileStorage entity = new FileStorage();
                        entity.setOriginalName(file.getOriginalFilename());
                        entity.setFilePath(storagePath);
                        entity.setMimeType(file.getContentType());
                        entity.setSizeInBytes(file.getSize());
                        entity.setUploadedBy(uploadedBy);
                        entity.setS3Key(key);
                        fileStorageRepository.save(entity);
                        return new FileUploadResponse(
                                entity.getId(),
                                entity.getOriginalName(),
                                entity.getFilePath(),
                                entity.getMimeType(),
                                entity.getSizeInBytes());
                    }
            );
        }catch (Exception e) {
            try {
                storageProvider.deleteFile(key);
            } catch (Exception ex) {
                log.error("error for delete file {}" ,ex.getMessage(),ex);
            }
            throw new FileUploadException("Failed to persist file metadata" ,e);
        }

    }


    public InternalFileResponse downloadFile(UUID id) {

        FileStorage fileStorage = fileStorageRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Record not found for ID: " + id, null));

        byte[] bytes =  s3Service.downloadFile(fileStorage.getS3Key());
        InternalFileResponse response = new InternalFileResponse();
        response.setFileName(fileStorage.getOriginalName());
        response.setExt(FileUtils.getFileExtension(fileStorage.getOriginalName()));
        response.setFile(bytes);
        response.setFileId(id);
        return response;

    }

}
