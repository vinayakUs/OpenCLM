package com.example.bff.exception;

import com.example.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class CustomAdvice {

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiResponse<?>> handleFileUploadException(FileUploadException ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        String errorCode = "INTERNAL_SERVER_ERROR";

        if (ex.getCause() instanceof WebClientResponseException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            errorCode = "UPLOAD_SERVICE_DOWN";
        }

        return ResponseEntity.status(status).body(
                ApiResponse.error(
                        ex.getMessage(),
                        errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error(
                        "Something went wrong", "INTERNAL_SERVER_ERROR"));
    }
}

