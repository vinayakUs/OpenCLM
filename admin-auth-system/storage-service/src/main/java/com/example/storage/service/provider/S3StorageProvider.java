package com.example.storage.service.provider;

import com.example.storage.exception.FileStorageException;
import io.awspring.cloud.s3.S3Exception;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

/**
 * Implementation of {@link StorageProvider} strategy.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageProvider implements StorageProvider{

    @Value("${app.bucket.name}")
    private String bucketName;

    private final S3Client s3Client;

    @Override
    public String uploadFile(String key, MultipartFile file) {
        if(file.isEmpty()){
            throw new IllegalArgumentException("Cannot upload empty file");
        }
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();
            s3Client.putObject(
                    putRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return "s3://" + bucketName + "/" + key;
        }catch (IOException e){
            log.error("Failed to read file input stream for key: {}", key, e);
            throw new FileStorageException("Failed to upload file to S3: IO Error",e);
        }catch (SdkClientException ex){
            log.error("Failed to upload file to S3: AWS Connection Error");
            throw new FileStorageException("Failed to upload file to S3: AWS Connection Error",ex);
        }catch (S3Exception e){
            log.error("S3 Upload failed for key: {}", key, e);
            throw new FileStorageException("Failed to upload file to S3: Service Error",e);
        }
    }

    @Override
    public byte[] downloadFile(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            return s3Client.getObjectAsBytes(request).asByteArray();
        }catch (SdkClientException ex){
            throw new FileStorageException("Failed to download file: AWS Connection Error",ex);
        } catch (S3Exception e){
            throw new FileStorageException("Failed to download file from S3", e);
        }
    }

    @Override
    public void deleteFile(String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
        }catch (SdkClientException ex){
            throw new FileStorageException("Failed to delete file: AWS Connection Error",ex);
        }catch (S3Exception e){
            log.error("Failed to delete file from S3: {}",key);
            throw new FileStorageException("Failed to delete file from S3",e);
        }
    }
}
