package com.example.common.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseV2<T> {

    @Builder.Default
    private boolean success = true;

    private T data;

    private ApiErrorV2 error;

    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    private String traceId; // Matches MDC/Sleuth trace ID
    private String path; // Request URI

    public static <T> ApiResponseV2<T> success(T data) {

        return ApiResponseV2.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponseV2<T> error(ApiErrorV2 error) {
        return ApiResponseV2.<T>builder()
                .success(false)
                .error(error)
                .build();
    }

}
