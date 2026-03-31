package com.example.bff.exception;

import java.io.File;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class FileUploadException extends RuntimeException {

    private final HttpStatus status;

    public FileUploadException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public FileUploadException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
