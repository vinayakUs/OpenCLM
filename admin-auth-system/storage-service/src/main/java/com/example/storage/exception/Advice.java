package com.example.storage.exception;

import com.example.common.dto.Api.ApiErrorV2;
import com.example.common.dto.Api.ApiResponseV2;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import com.example.common.shared.ApiErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class Advice {

        // Helper method to consolidate error building and standardize the MDC Trace ID
        private ResponseEntity<ApiResponseV2<Void>> buildErrorResponse(
                        String code, String message, List<String> details, HttpStatus status,
                        HttpServletRequest request) {

                String traceId = MDC.get("traceId");
                if (traceId == null || traceId.isBlank()) {
                        traceId = UUID.randomUUID().toString(); // Fallback for tests or missing context
                }
                ApiErrorV2 apiError = ApiErrorV2.builder()
                                .code(code)
                                .message(message)
                                .details(details) // Note: details is only supported in your REQUEST_BINDING_ERROR
                                .build();
                ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
                response.setTraceId(traceId);
                response.setPath(request.getRequestURI());
                return ResponseEntity.status(status).body(response);
        }

        // Overloaded helper for responses that don't need the 'details' list
        private ResponseEntity<ApiResponseV2<Void>> buildErrorResponse(
                        String code, String message, HttpStatus status, HttpServletRequest request) {
                return buildErrorResponse(code, message, null, status, request);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponseV2<Void>> handleException(Exception ex,
                        HttpServletRequest request) {

                log.error("Internal Error [TraceID: {}]: ", MDC.get("traceId"), ex);
                return buildErrorResponse(ApiErrorCode.INTERNAL_SERVER_ERROR.name(), "An unexpected error occurred.",
                                HttpStatus.INTERNAL_SERVER_ERROR, request);

        }

        /**
         * Method Not Allowed for Endpoint
         */
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleMethodNotSupportedException(
                        HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
                log.info("Method Not Supported [TraceID: {}]", MDC.get("traceId"));
                return buildErrorResponse(ApiErrorCode.METHOD_NOT_ALLOWED.name(),
                                ex.getMessage() != null ? ex.getMessage() : "Method Not Allowed",
                                HttpStatus.NOT_FOUND, request);
        }

        /**
         * NoHandlerFoundException
         */
        @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
        public ResponseEntity<ApiResponseV2<Void>> handleNotFoundExceptions(
                        Exception ex, HttpServletRequest request) {

                log.info("Resource/Endpoint Not Found [TraceID: {}]: {}", MDC.get("traceId"), ex.getMessage());
                return buildErrorResponse(ApiErrorCode.RESOURCE_NOT_FOUND.name(),
                                "The requested endpoint or resource could not be found.",
                                HttpStatus.NOT_FOUND, request);
        }

        /**
         * Exception for StorageProvider
         */
        @ExceptionHandler(FileStorageException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleFileStorageException(FileStorageException ex,
                        HttpServletRequest request) {

                log.error("Storage Error [TraceID: {}]: ", MDC.get("traceId"), ex);
                return buildErrorResponse(ApiErrorCode.STORAGE_ERROR.name(),
                                ex.getMessage() != null ? ex.getMessage() : "Operation failed due to storage error.",
                                HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        /**
         * File not found by id during download
         */
        @ExceptionHandler(FileNotFoundException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleFileNotFoundException(FileNotFoundException ex,
                        HttpServletRequest request) {

                log.warn("File Not Found [TraceID: {}]: {}", MDC.get("traceId"), ex.getMessage());
                return buildErrorResponse(ApiErrorCode.RESOURCE_NOT_FOUND.name(), ex.getMessage(), HttpStatus.NOT_FOUND,
                                request);
        }

        /**
         * Generic Exception for File Download Endpoint
         */
        @ExceptionHandler({ FileDownloadException.class })
        public ResponseEntity<ApiResponseV2<Void>> handleFileDownloadException(FileDownloadException ex,
                        HttpServletRequest request) {

                log.error("File Download Error [TraceID: {}]: ", MDC.get("traceId"), ex);
                return buildErrorResponse(ApiErrorCode.FILE_DOWNLOAD_ERROR.name(),
                                ex.getMessage() != null ? ex.getMessage() : "Operation Failed during File Download",
                                HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        /**
         * Generic Exception for File Upload Endpoint
         */
        @ExceptionHandler({ FileUploadException.class })
        public ResponseEntity<ApiResponseV2<Void>> handleFileUploadException(FileUploadException ex,
                        HttpServletRequest request) {

                log.error("File Upload Error [TraceID: {}]: ", MDC.get("traceId"), ex);
                return buildErrorResponse(ApiErrorCode.FILE_UPLOAD_ERROR.name(),
                                ex.getMessage() != null ? ex.getMessage() : "Operation Failed during File Upload",
                                HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        /**
         * Handle exception like missing request parameter
         */
        @ExceptionHandler(ServletRequestBindingException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleServletRequestBindingException(
                        ServletRequestBindingException ex,
                        HttpServletRequest request) {

                log.warn("Missing Param Found [TraceID: {}]: {}", MDC.get("traceId"), ex.getMessage());
                return buildErrorResponse(ApiErrorCode.REQUEST_BINDING_ERROR.name(),
                                "Request data is missing or invalid",
                                List.of(ex.getMessage()), HttpStatus.BAD_REQUEST, request);
        }

        /**
         * Missing File part exception
         */
        @ExceptionHandler(MissingServletRequestPartException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleMissingServletRequestPart(
                        MissingServletRequestPartException ex,
                        HttpServletRequest request) {

                log.warn("Missing request file part [TraceID: {}]: {}", MDC.get("traceId"), ex.getRequestPartName());
                return buildErrorResponse(ApiErrorCode.REQUEST_BINDING_ERROR.name(),
                                "Required multipart part is missing",
                                List.of("Missing part: " + ex.getRequestPartName()), HttpStatus.BAD_REQUEST, request);
        }

        /**
         * Generic File exception for FileDelete
         */
        @ExceptionHandler(FileDeleteException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleFileDeleteException(
                        FileDeleteException ex,
                        HttpServletRequest request) {

                log.error("File delete error [TraceID: {}]: ", MDC.get("traceId"), ex);
                return buildErrorResponse(ApiErrorCode.FILE_DELETE_ERROR.name(),
                                ex.getMessage() != null ? ex.getMessage() : "Operation Failed during File Delete",
                                HttpStatus.INTERNAL_SERVER_ERROR, request);
        }

        /**
         * Generic File exception for FileDelete
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex,
                        HttpServletRequest request) {

                log.warn("Parameter Type Mismatch [TraceID: {}]: Failed to convert '{}' to {}",
                                MDC.get("traceId"), ex.getValue(),
                                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");
                String errorMessage = String.format("Invalid parameter format: '%s' cannot be converted to %s",
                                ex.getName(),
                                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName()
                                                : "the required type");

                return buildErrorResponse(ApiErrorCode.INVALID_FIELD_VALUE.name(), errorMessage, HttpStatus.BAD_REQUEST,
                                request);
        }
}
