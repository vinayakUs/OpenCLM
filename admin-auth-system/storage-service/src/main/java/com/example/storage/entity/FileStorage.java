package com.example.storage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "file_storage")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FileStorage {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "storage_key" ,nullable = false)
    private String storageKey; // s3 key

    @Column(name = "file_path", nullable = false)
    private String filePath; // S3/MinIO path

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size_in_bytes")
    private Long sizeInBytes;

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy; // Fetched from logged-in user

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
