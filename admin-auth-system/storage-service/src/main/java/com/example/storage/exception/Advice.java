package com.example.storage.exception;

import com.example.common.dto.Api.ApiErrorV2;
import com.example.common.dto.Api.ApiResponseV2;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class Advice {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponseV2<Void>> handleException(Exception ex,
                        HttpServletRequest httpServletRequest) {

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

        /**
         * Exception for StorageProvider
         */
        @ExceptionHandler(FileStorageException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleFileStorageException(FileStorageException ex,
                        HttpServletRequest httpServletRequest) {

                String traceId = UUID.randomUUID().toString();
                log.error("Storage Error [TraceID: {}]: ", traceId, ex);

                ApiErrorV2 apiError = ApiErrorV2.builder()
                                .code("STORAGE_ERROR")
                                .message(ex.getMessage() != null ? ex.getMessage()
                                                : "Operation failed due to storage error.")
                                .build();
                ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
                response.setTraceId(traceId);
                response.setPath(httpServletRequest.getRequestURI());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        /**
         * File not found by id during download
         */
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

        /**
         * Generic Exception for File Download Endpoint
         */
        @ExceptionHandler({ FileDownloadException.class })
        public ResponseEntity<ApiResponseV2<Void>> handleFileDownloadException(FileDownloadException ex,
                                                                             HttpServletRequest request) {
            String traceId = UUID.randomUUID().toString();
            log.error("File Download Error [TraceID: {}]: ", traceId, ex);
            ApiErrorV2 apiError = ApiErrorV2.builder()
                    .code("FILE_DOWNLOAD_ERROR")
                    .message(ex.getMessage()!=null?ex.getMessage()
                            :"Operation Failed during File Download")
                    .build();
            ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
            response.setPath(request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        /**
         * Generic Exception for File Upload Endpoint
         */
        @ExceptionHandler({ FileUploadException.class })
        public ResponseEntity<ApiResponseV2<Void>> handleFileUploadException(FileUploadException ex,
                                                                             HttpServletRequest request) {
                String traceId = UUID.randomUUID().toString();
            log.error("File Upload Error [TraceID: {}]: ", traceId, ex);
            ApiErrorV2 apiError = ApiErrorV2.builder()
                        .code("FILE_UPLOAD_ERROR")
                        .message(ex.getMessage()!=null?ex.getMessage()
                                :"Operation Failed during File Upload")
                        .build();
                ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
                response.setPath(request.getRequestURI());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        /**
         * Handle exception like missing request parameter
         */
        @ExceptionHandler(ServletRequestBindingException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleServletRequestBindingException(
                        ServletRequestBindingException ex,
                        HttpServletRequest request) {

                String traceId = UUID.randomUUID().toString();
                log.warn("Missing Param Found [TraceID: {}]: {}", traceId, null);
                ApiErrorV2 apiError = ApiErrorV2.builder()
                                .code("REQUEST_BINDING_ERROR")
                                .message("Request data is missing or invalid")
                                .details(List.of(ex.getMessage()))
                                .build();
                ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
                response.setTraceId(traceId);
                response.setPath(request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        /**
         * Missing File part exception
         */
        @ExceptionHandler(MissingServletRequestPartException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleMissingServletRequestPart(
                        MissingServletRequestPartException ex,
                        HttpServletRequest request) {

                String traceId = UUID.randomUUID().toString();
                log.warn("Missing request file part [TraceID: {}]", traceId);

                ApiErrorV2 apiError = ApiErrorV2.builder()
                                .code("REQUEST_BINDING_ERROR")
                                .message("Required multipart part is missing")
                                .details(List.of("Missing part: " + ex.getRequestPartName()))
                                .build();

                ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
                response.setTraceId(traceId);
                response.setPath(request.getRequestURI());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        /**
         * Generic File exception for FileDelete
         */
        @ExceptionHandler(FileDeleteException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleFileDeleteException(
                FileDeleteException ex,
                HttpServletRequest request){

                String traceId = UUID.randomUUID().toString();
                log.error("File delete error [TraceID: {}]", traceId,ex);

                ApiErrorV2 apiError = ApiErrorV2.builder()
                        .code("FILE_DELETE_ERROR")
                        .message(ex.getMessage()!=null?ex.getMessage()
                                :"Operation Failed during File Delete")
                        .build();

                ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
                response.setTraceId(traceId);
                response.setPath(request.getRequestURI());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR ).body(response);
        }

        /**
         * Generic File exception for FileDelete
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponseV2<Void>> handleMethodArgumentTypeMismatchException(
                MethodArgumentTypeMismatchException ex,
                HttpServletRequest request){

                String traceId = UUID.randomUUID().toString();
                log.warn("Parameter Type Mismatch [TraceID: {}]: Failed to convert '{}' to {}",
                        traceId, ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "Unknown");
                String errorMessage = String.format("Invalid parameter format: '%s' cannot be converted to %s",
                        ex.getName(),
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "the required type");
                ApiErrorV2 apiError = ApiErrorV2.builder()
                        .code("INVALID_PARAMETER_FORMAT")
                        .message(errorMessage)
                        .build();
                ApiResponseV2<Void> response = ApiResponseV2.error(apiError);
                response.setTraceId(traceId);
                response.setPath(request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST ).body(response);
        }
}
