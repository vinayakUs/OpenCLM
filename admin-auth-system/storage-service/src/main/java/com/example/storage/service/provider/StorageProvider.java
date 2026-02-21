package com.example.storage.service.provider;


import com.example.storage.exception.FileStorageException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Strategy Implementation for Storage Operations.
 * <p>
 *     allow adding other implementation like s3, GC, Local Disk etc.
 * </p>
 */

public interface StorageProvider {

    /**
     * @param key The unique identifier of the file (eg "uploads/2023/image.png").
     * @param file File Content
     * @return The fully qualified path or URL to access the stored file.
     * @throws FileStorageException If the upload fails due to I/O errors or provider unavailability.
     */
    String uploadFile(String key, MultipartFile file);

    /**
     * Retrieves a file's content from storage layer.
     * @param key The unique identifier of the file to download.
     * @return The byter arrays
     * @throws FileStorageException If file cannot read or does not exist.
     */
    byte[] downloadFile(String key);

    /**
     * Delete file form Storage.
     * <p>
     *     Delete operation is Idempotent
     *     If key doesn't exist delete shouldn't throw error
     * </p>
     * @param key The unique identifier to delete.
     * @throws FileStorageException If the deletion fails due to provider issues.
     */
    void deleteFile(String key);
}
