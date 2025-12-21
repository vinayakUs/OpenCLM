package com.example.workflow.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {


    private boolean success;
    private ApiError error;
    private T data;


    public static <T> ApiResponse<T> success(T data){
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.success = true;
        apiResponse.data = data;
        return  apiResponse;
    }

    public static <T> ApiResponse<T> error(String message,String code , int status){
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.success = false;
        apiResponse.error = new ApiError(message,code,status);
        return  apiResponse;
    }

}
