package com.example.storageservice.services;

import com.example.storageservice.dto.FileUploadResponse;
import com.example.storageservice.entity.FileStorage;
import com.example.storageservice.repository.FileStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Service s3Service;
    private final FileStorageRepository fileStorageRepository;

    public FileUploadResponse uploadFile(MultipartFile file, UUID uploadedBy) {

        String folder =     "uploads/" + LocalDate.now() + "/" ;
        String s3Path = s3Service.uploadFile(folder,file);

//        save metadata in db
        FileStorage  entity = new FileStorage();
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
                entity.getSizeInBytes()
        );


    }


}
