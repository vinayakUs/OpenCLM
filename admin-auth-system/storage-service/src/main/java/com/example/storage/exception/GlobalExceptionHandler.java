package com.example.storage.exception;

import com.example.common.dto.ApiErrorV2;
import com.example.common.dto.ApiResponseV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseV2<Void>> handleIllegalArgumentException(IllegalArgumentException ex,
            HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("Bad Request [TraceID: {}]: {}", traceId, ex.getMessage());

        ApiErrorV2 apiError = ApiErrorV2.builder()
                .code("BAD_REQUEST")
                .message(ex.getMessage())
                .build();

        ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
        response.setTraceId(traceId);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiResponseV2<Void>> handleFileNotFoundException(FileNotFoundException ex,
            HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.warn("File Not Found [TraceID: {}]: {}", traceId, ex.getMessage());

        ApiErrorV2 apiError = ApiErrorV2.builder()
                .code("FILE_NOT_FOUND")
                .message(ex.getMessage())
                .build();

        ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
        response.setTraceId(traceId);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiResponseV2<Void>> handleFileStorageException(FileStorageException ex,
            HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("Storage Error [TraceID: {}]: ", traceId, ex);

        ApiErrorV2 apiError = ApiErrorV2.builder()
                .code("STORAGE_ERROR")
                .message("Operation failed due to storage error.")
                .build();

        ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
        response.setTraceId(traceId);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseV2<Void>> handleException(Exception ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("Internal Error [TraceID: {}]: ", traceId, ex);

        ApiErrorV2 apiError = ApiErrorV2.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred.")
                .build();

        ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
        response.setTraceId(traceId);
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
