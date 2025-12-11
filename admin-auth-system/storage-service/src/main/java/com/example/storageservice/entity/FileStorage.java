package com.example.storageservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_storage")
@Data
public class FileStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "original_name",nullable = false)
    private String originalName;

    @Column(name = "file_path", nullable = false)
    private String filePath;      // S3/MinIO path

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size_in_bytes")
    private Long sizeInBytes;

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;      // Fetched from logged-in user

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}

//
//CREATE TABLE file_storage (
//        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
//
//original_name VARCHAR(255) NOT NULL,
//file_path TEXT NOT NULL,                    -- S3/MinIO path
//mime_type VARCHAR(100),
//size_in_bytes BIGINT,
//
//uploaded_by UUID,                           -- user who uploaded
//created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
//);
//
//        -- 🔹 Indexes for optimized queries
//CREATE INDEX idx_file_storage_uploaded_by ON file_storage(uploaded_by);
//CREATE INDEX idx_file_storage_created_at  ON file_storage(created_at);
//
//-- For faster lookups by file_path (if needed)
//CREATE INDEX idx_file_storage_file_path ON file_storage USING hash (file_path);
