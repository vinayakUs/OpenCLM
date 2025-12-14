package com.example.storageservice.repository;

import com.example.storageservice.dto.FileUploadResponse;
import com.example.storageservice.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileStorageRepository extends JpaRepository<FileStorage, UUID> {
}
