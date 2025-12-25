package com.example.common.dto;

import lombok.Data;

@Data
public class ApiError {
    private String message;
    private String status;

    public ApiError(String message, String status) {
        this.message = message;
        this.status = status;
    }
}
