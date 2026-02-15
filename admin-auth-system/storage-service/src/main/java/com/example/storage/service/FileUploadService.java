package com.example.storage.service;

import com.example.storage.dto.FileUploadResponse;
import com.example.storage.entity.FileStorage;
import com.example.storage.repository.FileStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Service s3Service;
    private final FileStorageRepository fileStorageRepository;

    public FileUploadResponse uploadFile(MultipartFile file, UUID uploadedBy) {

        String folder = "uploads/" + LocalDate.now() + "/";
        String s3Path = s3Service.uploadFile(folder, file);

        // save metadata in db
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

    }

    public byte[] downloadFile(String keyName,String bucketName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(keyName)
                .bucket(bucketName)
                .build();
        CompletableFuture<ResponseBytes<GetObjectResponse>> response = getAsyncClient().getObject(objectRequest, AsyncResponseTransformer.toBytes());
    }

}
