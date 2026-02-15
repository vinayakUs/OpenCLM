package com.example.bff.exception;

import com.example.bff.exception.comman.DownstreamTimeoutException;
import com.example.bff.exception.comman.DownstreamUnavailableException;
import com.example.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(WebExchangeBindException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(errorMessage, "VALIDATION_ERROR"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(errorMessage, "VALIDATION_ERROR"));
    }

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

    @ExceptionHandler(DownstreamUnavailableException.class)
    public ResponseEntity<ApiResponse<?>> handleDownstreamUnavailableException(DownstreamUnavailableException ex) {

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                ApiResponse.error(
                        ex.getMessage(), "SERVICE_UNAVAILABLE"));
    }

    @ExceptionHandler(DownstreamTimeoutException.class)
    public ResponseEntity<ApiResponse<?>> handleTimeout(DownstreamTimeoutException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(ApiResponse.error(ex.getMessage(), "TIMEOUT"));
    }

}
