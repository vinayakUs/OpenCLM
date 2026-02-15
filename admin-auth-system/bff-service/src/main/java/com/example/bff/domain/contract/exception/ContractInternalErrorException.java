package com.example.bff.domain.contract.exception;

public class ContractInternalErrorException extends RuntimeException {
    public ContractInternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
