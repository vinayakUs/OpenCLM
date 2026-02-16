package com.example.storage.exception;

import com.example.common.dto.Api.ApiErrorV2;
import com.example.common.dto.Api.ApiResponseV2;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.List;
import java.util.UUID;

@ControllerAdvice
@Slf4j
public class Advice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseV2<Void>> handleException(Exception ex, HttpServletRequest httpServletRequest){

        String traceId = UUID.randomUUID().toString();
        log.error("Internal Error [TraceID: {}]: ", traceId, ex);

        ApiErrorV2 apiError = ApiErrorV2.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred.")
                .build();
        ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
        response.setTraceId(traceId);
        response.setPath(httpServletRequest.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiResponseV2<Void>> handleFileStorageException(FileStorageException ex, HttpServletRequest httpServletRequest){

        String traceId = UUID.randomUUID().toString();
        log.error("Storage Error [TraceID: {}]: ", traceId, ex);

        ApiErrorV2 apiError = ApiErrorV2.builder()
                .code("STORAGE_ERROR")
                .message("Operation failed due to storage error.")
                .build();
        ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
        response.setTraceId(traceId);
        response.setPath(httpServletRequest.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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

    //Params Not found validation
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseV2<Void>> handleMissingParam(
            MissingServletRequestParameterException ex,
            HttpServletRequest request) {


        String traceId = UUID.randomUUID().toString();
        log.warn("Missing Param Found [TraceID: {}]: {}", traceId, null);
        ApiErrorV2 apiError = ApiErrorV2.builder()
                .code("VALIDATION_ERROR")
                .message("Missing Parameters")
                .details(List.of("Missing Parameter " + ex.getParameterName()))
                .build();
        ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
        response.setTraceId(traceId);
        response.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }}
