package com.example.common.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {

    public boolean success;
    public ApiError error;
    public T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.data = data;
        apiResponse.success = true;
        return apiResponse;
    }

    public static <T> ApiResponse<T> error(String message, String status) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.success = false;
        apiResponse.error = new ApiError(message, status);
        return apiResponse;
    }

}
