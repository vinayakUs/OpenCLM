package com.example.storage.repository;

import com.example.storage.entity.FileStorage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileStorageRepository extends JpaRepository<FileStorage, UUID> {
}
