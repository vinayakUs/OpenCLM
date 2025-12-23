package com.example.workflow.dto;

import lombok.Data;

@Data
public class ApiError {
    private String message;
    private String code;
    private int status;

    public ApiError(String message, String code, int status) {
        this.message = message;
        this.code = code;
        this.status = status;
    }
}
