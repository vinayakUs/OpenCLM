package com.example.bff.domain.workflow.exception;

import com.example.bff.shared.ApiErrorCode;
import com.example.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WorkflowAdvice {

    @ExceptionHandler(WorkflowCreationException.class)
    public ResponseEntity<ApiResponse<?>> handleWorkflowCreationException(WorkflowCreationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(
                        ApiResponse.error("", ApiErrorCode.BAD_GATEWAY.name())
                );
    }

    @ExceptionHandler(WorkflowRetrievalException.class)
    public ResponseEntity<ApiResponse<?>> handleWorkflowRetrievalException(WorkflowRetrievalException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(
                        ApiResponse.error("", ApiErrorCode.BAD_GATEWAY.name())
                );
    }

}

