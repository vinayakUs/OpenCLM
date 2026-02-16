package com.example.storage.service;

import com.example.storage.dto.FileUploadResponse;
import com.example.storage.dto.InternalFileResponse;
import com.example.storage.entity.FileStorage;
import com.example.storage.exception.FileNotFoundException;
import com.example.storage.exception.FileStorageException;
import com.example.storage.repository.FileStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    private final S3Service s3Service;
    private final FileStorageRepository fileStorageRepository;

    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, UUID uploadedBy) {

        String key = buildKey(file);

        try {
            String s3Path = s3Service.uploadFile(key, file);

            FileStorage entity = new FileStorage();
            entity.setOriginalName(file.getOriginalFilename());
            entity.setFilePath(s3Path);
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
        } catch (Exception e) {
            s3Service.deleteFile(key);
            throw new FileStorageException("Failed to save file metadata, rolled back S3 upload" ,e);
        }

    }

    private String buildKey(MultipartFile file){

        String extension = getFileExtension(file.getOriginalFilename());
        String id = UUID.randomUUID()
                .toString()
                .replace("-","");

        //Shard prefix
        String p1 = id.substring(0,2);
        String p2 = id.substring(2,4);

        return "uploads/"+p1+"/"+p2+"/"+id+extension;
    }

    private String getFileExtension(String fileName){
        if(fileName == null || fileName.isBlank()){
            return "";
        }

        int dot = fileName.lastIndexOf('.');
        if(dot<0 || dot == fileName.length()-1){
            return "";
        }
        return  fileName.substring(dot);
    }



    public InternalFileResponse downloadFile(UUID id) {

        FileStorage fileStorage = fileStorageRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("Record not found for ID: " + id, null));

        byte[] bytes =  s3Service.downloadFile(fileStorage.getS3Key());
        InternalFileResponse response = new InternalFileResponse();
        response.setFileName(fileStorage.getOriginalName());
        response.setExt(getFileExtension(fileStorage.getOriginalName()));
        response.setFile(bytes);
        response.setFileId(id);
        return response;

    }

}
