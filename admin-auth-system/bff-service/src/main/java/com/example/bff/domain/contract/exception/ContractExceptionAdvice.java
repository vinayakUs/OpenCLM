package com.example.bff.domain.contract.exception;

import com.example.bff.shared.ApiErrorCode;
import com.example.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ContractExceptionAdvice {

    @ExceptionHandler(ContractInternalErrorException.class)
    public ResponseEntity<ApiResponse<?>> handleContractInternalErrorException(ContractInternalErrorException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error(
                        ex.getMessage(), ApiErrorCode.INTERNAL_SERVER_ERROR.name())
        );
    }


    @ExceptionHandler(ContractException.class)
    public ResponseEntity<ApiResponse<?>> handleContractException(ContractException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(ex.getMessage(), ApiErrorCode.INVALID_REQUEST.name())
        );
    }

}
